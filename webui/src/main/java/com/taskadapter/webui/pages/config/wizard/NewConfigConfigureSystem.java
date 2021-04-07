package com.taskadapter.webui.pages.config.wizard;

import com.taskadapter.config.StorageException;
import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.ConnectorSetup;
import com.taskadapter.connector.definition.FileSetup;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.web.ConnectorSetupPanel;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.service.Sandbox;
import com.taskadapter.web.uiapi.SetupId;
import com.taskadapter.webui.ConfigOperations;
import com.taskadapter.webui.Page;
import com.taskadapter.webui.pages.WizardStep;
import com.taskadapter.webui.service.EditorManager;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NewConfigConfigureSystem implements WizardStep<SetupId> {
    private final EditorManager editorManager;
    private final ConfigOperations configOps;
    private final Sandbox sandbox;
    private final Function<SetupId, Void> setupSelected;

    public NewConfigConfigureSystem(EditorManager editorManager, ConfigOperations configOps,
                                    Sandbox sandbox, Function<SetupId, Void> setupSelected) {
        this.editorManager = editorManager;
        this.configOps = configOps;
        this.sandbox = sandbox;
        this.setupSelected = setupSelected;
    }

    @Override
    public SetupId getResult() {
        return null;
    }

    @Override
    public Component ui(Object connectorId) {
        return createSetupPanelForConnector((String) connectorId);
    }

    private ChooseOrCreateSetupFragment createSetupPanelForConnector(String connectorId) {

        var setups = configOps.getAllConnectorSetups(connectorId);
        var setupUiItems = setups.stream().map(s -> {
            if (s instanceof WebConnectorSetup) {
                WebConnectorSetup webSetup = (WebConnectorSetup) s;
                return new ExistingSetup(new SetupId(webSetup.getId()),
                        String.format("%1$s (%2$s)", webSetup.getConnectorId(), webSetup.getHost()));
            } else {
                FileSetup fileSetup = (FileSetup) s;
                return new ExistingSetup(new SetupId(fileSetup.getId()), fileSetup.getLabel());
            }
        }).collect(Collectors.toList());

        // if you remove these class declarations, you will get runtime ClassCastExceptions saying cannot convert
        // WebConnectorSetup to Nothing!
        PluginEditorFactory<ConnectorConfig, ConnectorSetup> editor = editorManager.getEditorFactory(connectorId);
        ConnectorSetup setup = editor.createDefaultSetup(sandbox);
        var editSetupPanel = editor.getEditSetupPanel(sandbox, setup);
        var addNewButton = new Button();
        return new ChooseOrCreateSetupFragment(configOps, setupUiItems, addNewButton, editSetupPanel, setupSelected);
    }
}

class ExistingSetup {
    private final SetupId id;
    private final String description;

    ExistingSetup(SetupId id, String description) {
        this.id = id;
        this.description = description;
    }

    public SetupId getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
}

class ChooseOrCreateSetupFragment extends VerticalLayout {
    private List<ExistingSetup> setups;
    private Button button;
    private ConnectorSetupPanel connectorSetupPanel;

    private static final String selectExistingLabel = Page.message("createConfigPage.selectExistingOrNew");

    private final Label errorMessageLabel = new Label();

    private boolean inSelectMode = true;

    private final Select<ExistingSetup> selectPanel;
    private final Component connectorSetupPanelUI;

    public ChooseOrCreateSetupFragment(ConfigOperations configOps,
                                       List<ExistingSetup> setups,
                                       Button button, ConnectorSetupPanel connectorSetupPanel,
                                       Function<SetupId, Void> setupSelected) {
        this.setups = setups;
        this.button = button;
        this.connectorSetupPanel = connectorSetupPanel;
        selectPanel = createSavedServerConfigurationsSelector(setups);
        errorMessageLabel.addClassName("error-message-label");

        connectorSetupPanelUI = connectorSetupPanel.getComponent();
        var orCreateNewLabel = new Label(Page.message("createConfigPage.orCreateNew"));

        if (!setups.isEmpty()) {
            selectPanel.setValue(setups.get(0));
        } else {
            inSelectMode = false;
            button.setEnabled(false);
        }

        var nextButton = new Button(Page.message("newConfig.next"),
                event -> {
                    if (inEditMode()) {
                        var maybeString = connectorSetupPanel.validate();
                        if (maybeString.isEmpty()) {
                            SetupId setupId = null;
                            try {
                                setupId = configOps.saveNewSetup(connectorSetupPanel.getResult());
                            } catch (StorageException storageException) {
                                throw new RuntimeException(storageException);
                            }
                            setupSelected.apply(setupId);
                        } else {
                            errorMessageLabel.setText(maybeString.get());
                        }
                    } else {
                        var setupId = selectPanel.getValue().getId();
                        setupSelected.apply(setupId);
                    }
                }
        );

        add(selectPanel, orCreateNewLabel, button, connectorSetupPanelUI, errorMessageLabel,
                nextButton);
        button.addClickListener(e -> {
            inSelectMode = !inSelectMode;
            refresh();
        });

        refresh();
    }

    private Select<ExistingSetup> createSavedServerConfigurationsSelector(List<ExistingSetup> savedSetups) {
        var combobox = new Select<ExistingSetup>();
        combobox.setMinWidth("500px");
        combobox.setLabel(selectExistingLabel);
        combobox.setEmptySelectionAllowed(false);
        combobox.setItemLabelGenerator(ExistingSetup::getDescription);
        combobox.setItems(savedSetups);
        return combobox;
    }

    private boolean inEditMode() {
        return !inSelectMode;
    }

    private void refresh() {
        connectorSetupPanelUI.setVisible(!inSelectMode);
        selectPanel.setVisible(inSelectMode);
        var caption = inSelectMode ? Page.message("createConfigPage.button.createNew")
                : Page.message("createConfigPage.button.selectExisting");
        button.setText(caption);
    }

    private Optional<String> validateSelectMode() {
        if (selectPanel.getValue() == null) {
            return Optional.of(Page.message("createConfigPage.error.mustSelectOrCreate"));
        }
        return Optional.empty();
    }
}
