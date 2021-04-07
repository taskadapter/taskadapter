package com.taskadapter.webui.pages.config.wizard;

import com.taskadapter.PluginManager;
import com.taskadapter.config.StorageException;
import com.taskadapter.web.service.Sandbox;
import com.taskadapter.web.uiapi.ConfigId;
import com.taskadapter.web.uiapi.SetupId;
import com.taskadapter.webui.BasePage;
import com.taskadapter.webui.ConfigOperations;
import com.taskadapter.webui.Layout;
import com.taskadapter.webui.Page;
import com.taskadapter.webui.SessionController;
import com.taskadapter.webui.config.SelectConnectorComponent;
import com.taskadapter.webui.pages.Navigator;
import com.taskadapter.webui.pages.WizardStep;
import com.taskadapter.webui.service.EditorManager;
import com.taskadapter.webui.service.Preservices;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.Route;

import java.util.Optional;
import java.util.function.Function;

@Route(value = Navigator.NEW_CONFIG, layout = Layout.class)
@CssImport(value = "./styles/views/mytheme.css")
public class NewConfigPage extends BasePage {

    private ConfigOperations configOps = SessionController.buildConfigOperations();
    private Preservices services = SessionController.getServices();
    private Sandbox sandbox = SessionController.createSandbox();
    private PluginManager pluginManager = services.pluginManager;
    private EditorManager editorManager = services.editorManager;

    private final WizardPanel wizard = new WizardPanel();

    private Label errorMessageLabel = new Label("");

    private Optional<String> connector1Id = Optional.empty();
    private Optional<SetupId> connector1SetupId = Optional.empty();
    private Optional<String> connector2Id = Optional.empty();
    private Optional<SetupId> connector2SetupId = Optional.empty();
    private Optional<String> description = Optional.empty();

    public NewConfigPage() {
        buildUi();
    }

    private void buildUi() {
        wizard.registerStep(new SelectConnectorWizardStep(pluginManager,
                connectorId -> {
                    connector1Id = Optional.of(connectorId);
                    wizard.showStep(2);
                    return null;
                }
        ));

        wizard.registerStep(new NewConfigConfigureSystem(editorManager, configOps, sandbox,
                label -> {
                    connector1SetupId = Optional.of(label);
                    wizard.showStep(3);
                    return null;
                }
        ));

        wizard.registerStep(new SelectConnectorWizardStep(pluginManager,
                connectorId -> {
                    connector2Id = Optional.of(connectorId);
                    wizard.showStep(4);
                    return null;
                }
        ));

        wizard.registerStep(new NewConfigConfigureSystem(editorManager, configOps, sandbox,
                label -> {
                    connector2SetupId = Optional.of(label);
                    wizard.showStep(5);
                    return null;
                }
        ));

        wizard.registerStep(new NewConfigGiveDescription(
                d -> {
                    description = Optional.of(d);
                    saveClicked();
                    return null;
                }
        ));
        wizard.showStep(1);

        add(new Label(Page.message("createConfigPage.createNewConfig")),
                errorMessageLabel,
                wizard);
    }

    private void saveClicked() {
        try {
            var configId = save();
            Navigator.configsList();
        } catch (StorageException e) {
            errorMessageLabel.setText(Page.message("createConfigPage.failedToSave"));
        }
    }

    private ConfigId save() throws StorageException {
        var descriptionString = description.get();
        var id1 = connector1Id.get();
        var id2 = connector2Id.get();
        return configOps.createNewConfig(descriptionString, id1, connector1SetupId.get(), id2, connector2SetupId.get());
    }
}

class SelectConnectorWizardStep implements WizardStep<String> {
    private final PluginManager pluginManager;
    private final Function<String, Void> next;

    String result = "";

    public SelectConnectorWizardStep(PluginManager pluginManager, Function<String, Void> next) {
        this.pluginManager = pluginManager;
        this.next = next;
    }

    public String getResult() {
        return result;
    }

    public Component ui(Object any) {
        return new SelectConnectorComponent(pluginManager, (connectorId) -> {
            result = connectorId;
            next.apply(connectorId);
            return null;
        });
    }
}