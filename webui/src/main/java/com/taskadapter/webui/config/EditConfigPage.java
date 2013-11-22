package com.taskadapter.webui.config;

import com.taskadapter.config.StorageException;
import com.taskadapter.connector.definition.MappingSide;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.model.GTaskDescriptor;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.ButtonBuilder;
import com.taskadapter.webui.CloneDeletePanel;
import com.taskadapter.webui.Page;
import com.taskadapter.webui.data.ExceptionFormatter;
import com.taskadapter.webui.export.Exporter;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.vaadin.server.Sizeable.Unit.PERCENTAGE;

public class EditConfigPage extends Page {
    private final Logger logger = LoggerFactory.getLogger(EditConfigPage.class);

    private VerticalLayout layout = new VerticalLayout();
    private UISyncConfig config;

    private Label errorMessageLabel = new Label();
    private OnePageEditor editor;

    private void buildUI() {
        layout.removeAllComponents();
        layout.setSpacing(true);
        createTopButtons();
        createEditDescriptionElement();
        createMainEditor();
        createErrorArea();
        createBottomButtons();
    }

    private void createMainEditor() {
        editor = new OnePageEditor(MESSAGES, services.createCurrentUserSandbox(), config);
        addExportButtonListeners();
        layout.addComponent(editor);
    }

    private void addExportButtonListeners() {
        addExportButtonListener(editor.getButtonLeft(), MappingSide.LEFT);
        addExportButtonListener(editor.getButtonRight(), MappingSide.RIGHT);
    }

    private void addExportButtonListener(Button button, MappingSide exportDirection) {
        UISyncConfig configForExport = DirectionResolver.getDirectionalConfig(config, exportDirection);
        final Exporter exporter = new Exporter(MESSAGES, services, navigator, configForExport);
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                exportClicked(exporter);
            }
        });
    }

    private void createEditDescriptionElement() {
        HorizontalLayout descriptionLayout = new HorizontalLayout();
        descriptionLayout.setSpacing(true);
        layout.addComponent(descriptionLayout);
        descriptionLayout.addComponent(new Label(MESSAGES.get("editConfig.description")));
        TextField descriptionField = new TextField();
        descriptionField.addStyleName("configEditorDescriptionLabel");
        MethodProperty<String> label = new MethodProperty<String>(config, "label");
        descriptionField.setPropertyDataSource(label);
        descriptionLayout.addComponent(descriptionField);
    }

    private void createTopButtons() {
        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setWidth(100, PERCENTAGE);
        CloneDeletePanel cloneDeletePanel = new CloneDeletePanel(services, navigator, config);
        buttonsLayout.addComponent(cloneDeletePanel);
        buttonsLayout.setComponentAlignment(cloneDeletePanel, Alignment.MIDDLE_RIGHT);
        layout.addComponent(buttonsLayout);
    }

    private void createErrorArea() {
        errorMessageLabel.addStyleName("error-message-label");
        errorMessageLabel.setWidth(100, PERCENTAGE);
        layout.addComponent(errorMessageLabel);
    }

    private void createBottomButtons() {
        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setWidth(100, PERCENTAGE);
        HorizontalLayout rightLayout = new HorizontalLayout();
        buttonsLayout.addComponent(rightLayout);
        buttonsLayout.setComponentAlignment(rightLayout, Alignment.BOTTOM_RIGHT);

        Button saveButton = new Button(MESSAGES.get("button.save"));
        saveButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                saveClicked();
            }
        });
        rightLayout.addComponent(saveButton);
        rightLayout.addComponent(ButtonBuilder.createBackButton(navigator, MESSAGES.get("button.close")));
        layout.addComponent(buttonsLayout);
    }

    private void saveClicked() {
        if (validate()) {
            save();
            Notification.show("", MESSAGES.get("editConfig.messageSaved"), Notification.Type.HUMANIZED_MESSAGE);
        }
    }

    private void exportClicked(Exporter exporter) {
        if (validate()) {
            save();
            exporter.export();
        }
    }

    public void setConfig(UISyncConfig config) {
        this.config = config.normalized();
    }

    private boolean validate() {
        errorMessageLabel.setValue("");
        try {
            editor.validate();
        } catch (FieldAlreadyMappedException e) {
            String s = MESSAGES.format("editConfig.error.fieldAlreadyMapped", e.getValue());
            errorMessageLabel.setValue(s);
            return false;
        } catch (FieldNotMappedException e) {
            String fieldDisplayName = GTaskDescriptor.getDisplayValue(e.getField());
            String s = MESSAGES.format("error.fieldSelectedForExportNotMapped", fieldDisplayName);
            errorMessageLabel.setValue(s);
            return false;
        } catch (BadConfigException e) {
            String s = ExceptionFormatter.format(e);
            errorMessageLabel.setValue(s);
            return false;
        }
        return true;
    }

    private void save() {
        try {
            services.getUIConfigStore().saveConfig(config);
        } catch (StorageException e) {
            String message = MESSAGES.format("editConfig.error.cantSave", e.getMessage());
            errorMessageLabel.setValue(message);
            logger.error(message, e);
        }
    }

    @Override
    public String getPageGoogleAnalyticsID() {
        return "edit_config";
    }

    @Override
    public Component getUI() {
        buildUI();
        return layout;
    }

    public void setErrorMessage(String errorMessage) {
        errorMessageLabel.setValue(errorMessage);
    }
}
