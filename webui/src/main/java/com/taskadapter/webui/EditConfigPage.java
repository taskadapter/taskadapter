package com.taskadapter.webui;

import com.taskadapter.config.StorageException;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.*;

public class EditConfigPage extends Page {
    private VerticalLayout layout = new VerticalLayout();
    private UISyncConfig config;
    private TextField configDescription;
    private OnePageEditor onePageEditor;

    private Label errorMessageLabel = new Label("Test");

    private void buildUI() {
        layout.removeAllComponents();
        layout.setSpacing(true);
        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setWidth(100, Sizeable.UNITS_PERCENTAGE);

        Button saveButton = new Button("Save");
        saveButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                save();
            }
        });
        buttonsLayout.addComponent(saveButton);

        buttonsLayout.addComponent(PageUtil.createButton(navigator, "Cancel", new ConfigsPage()));

        errorMessageLabel.addStyleName("error-message-label");
        errorMessageLabel.setWidth(100, Sizeable.UNITS_PERCENTAGE);
        buttonsLayout.addComponent(errorMessageLabel);
        buttonsLayout.setExpandRatio(errorMessageLabel, 1.0f);

        CloneDeletePanel cloneDeletePanel = new CloneDeletePanel(services, navigator, config, new CloneDeletePanel.Callback() {
            @Override
            public boolean onCloneConfig() {
                return validateEditor();
            }
        });
        buttonsLayout.addComponent(cloneDeletePanel);
        buttonsLayout.setComponentAlignment(cloneDeletePanel, Alignment.MIDDLE_RIGHT);
        layout.addComponent(buttonsLayout);

        HorizontalLayout descriptionLayout = new HorizontalLayout();
        descriptionLayout.setSpacing(true);
        layout.addComponent(descriptionLayout);
        descriptionLayout.addComponent(new Label("Description:"));
        configDescription = new TextField();
        configDescription.addStyleName("configEditorDescriptionLabel");
        configDescription.setValue(config.getLabel());
        descriptionLayout.addComponent(configDescription);

        onePageEditor = new OnePageEditor(services, config);
        layout.addComponent(onePageEditor);
    }

    public void setConfig(UISyncConfig config) {
        this.config = config.normalized();
    }

    private void save() {
        if (validateEditor()) {
//            updateFileWithDataInForm();
            String userLoginName = services.getAuthenticator().getUserName();
            try {
                services.getUIConfigStore().saveConfig(userLoginName, config);
            } catch (StorageException e) {
                // FIXME:
                // TODO !!! 
                // Write some message to a user.
                errorMessageLabel.setValue("Failed to save config"
                        + e.getMessage());
                navigator.showNotification("Failed to save config",
                        "Failed to save config");
                return;
            }
            navigator.showNotification("Saved", "All is saved OK");

            errorMessageLabel.setValue("");
            navigator.show(new ConfigsPage());
        }
    }

    private boolean validateEditor() {
        // TODO !!! delete
/*        try {
            panel1.validateAll();
        } catch (ValidationException e) {
            errorMessageLabel.setValue(e.getMessage());
            tabSheet.setSelectedTab(panel1);
            return false;
        }

        try {
            panel2.validateAll();
        } catch (ValidationException e) {
            errorMessageLabel.setValue(e.getMessage());
            tabSheet.setSelectedTab(panel2);
            return false;
        }
*/
        return true;
    }

//    private void updateFileWithDataInForm() {
//        ConnectorConfig c1 = panel1.getConfig();
//        ConnectorConfig c2 = panel2.getConfig();
//        ConnectorDataHolder d1 = new ConnectorDataHolder(file.getConnectorDataHolder1().getType(), c1);
//        ConnectorDataHolder d2 = new ConnectorDataHolder(file.getConnectorDataHolder2().getType(), c2);

//        file.setConfigLabel((String) configDescription.getValue());
//        file.setConnectorDataHolder1(d1);
//        file.setConnectorDataHolder2(d2);
//    }

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
