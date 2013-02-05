package com.taskadapter.webui;

import com.taskadapter.config.StorageException;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;

import java.util.Iterator;

public class NewConfigPage extends Page {
    private static final String DESCRIPTION_HINT = "(optional)";
    private static final String SYSTEM_1_TITLE = "System 1";
    private static final String SYSTEM_2_TITLE = "System 2";
    private static final String SELECT_CONNECTOR_1_MESSAGE = "Please select " + SYSTEM_1_TITLE;
    private static final String SELECT_CONNECTOR_2_MESSAGE = "Please select " + SYSTEM_2_TITLE;

    private TextField descriptionTextField;
    private ListSelect connector1;
    private ListSelect connector2;
    private Panel panel;
    private Label errorMessageLabel;

    public NewConfigPage() {
        buildUI();
    }

    private void buildUI() {
        panel = new Panel("Create new config");
        panel.setWidth("600px");

        GridLayout grid = new GridLayout(2, 4);
        grid.setSpacing(true);
        grid.setMargin(true);
        panel.setContent(grid);

        connector1 = new ListSelect(SYSTEM_1_TITLE);
        connector1.setRequired(true);
        connector1.setNullSelectionAllowed(false);
        grid.addComponent(connector1, 0, 0);

        connector2 = new ListSelect(SYSTEM_2_TITLE);
        connector2.setRequired(true);
        connector2.setNullSelectionAllowed(false);
        grid.addComponent(connector2, 1, 0);

        descriptionTextField = new TextField("Description");
        descriptionTextField.setInputPrompt(DESCRIPTION_HINT);
        descriptionTextField.setWidth("100%");

        grid.addComponent(descriptionTextField, 0, 1, 1, 1);
        grid.setComponentAlignment(descriptionTextField, Alignment.MIDDLE_CENTER);

        // empty label by default
        errorMessageLabel = new Label();
        errorMessageLabel.addStyleName("error-message-label");

        Button saveButton = new Button("Create");
        saveButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                saveClicked();
            }
        });

        grid.addComponent(errorMessageLabel, 0, 2, 1, 2);
        grid.addComponent(saveButton, 1, 3);
        grid.setComponentAlignment(saveButton, Alignment.MIDDLE_RIGHT);
    }

    private void loadDataConnectors() {
        Iterator<Descriptor> connectors = services.getPluginManager().getPluginDescriptors();
        while (connectors.hasNext()) {
            Descriptor connector = connectors.next();
            String id = connector.getID();
            String label = connector.getLabel();
            connector1.addItem(id);
            connector1.setItemCaption(id, label);

            connector2.addItem(id);
            connector2.setItemCaption(id, label);
        }
        connector1.setRows(connector1.size());
        connector2.setRows(connector2.size());
    }

    private void saveClicked() {
        try {
            validate();
            errorMessageLabel.setValue("");
            final UISyncConfig saveResult = save();
            showTaskDetailsPage(saveResult);

            //clear for new config
            descriptionTextField.setValue("");
        } catch (ConnectorNotSelectedException e) {
            errorMessageLabel.setValue(e.getMessage());
        } catch (StorageException e) {
            errorMessageLabel.setValue("Failed to save config in persistent store");
        }
    }

    private void showTaskDetailsPage(UISyncConfig config) {
        navigator.showConfigureTaskPage(config);
    }

    private void validate() throws ConnectorNotSelectedException {
        if (connector1.getValue() == null) {
            connector1.setRequiredError(SELECT_CONNECTOR_1_MESSAGE);
            throw new ConnectorNotSelectedException(SELECT_CONNECTOR_1_MESSAGE);
        } else {
            connector1.setRequiredError("");
        }

        if (connector2.getValue() == null) {
            connector1.setRequiredError(SELECT_CONNECTOR_2_MESSAGE);
            throw new ConnectorNotSelectedException(SELECT_CONNECTOR_2_MESSAGE);
        } else {
            connector2.setRequiredError("");
        }
    }

    private UISyncConfig save() throws StorageException {

        final String descriptionString = descriptionTextField.getValue();
        final String id1 = (String) connector1.getValue();
        final String id2 = (String) connector2.getValue();
        final String currentUserLoginName = services.getCurrentUserInfo().getUserName();

        return services.getUIConfigStore()
                .createNewConfig(currentUserLoginName, descriptionString, id1, id2);
    }

    @Override
    public String getPageGoogleAnalyticsID() {
        return "create_config";
    }

    @Override
    public Component getUI() {
        loadDataConnectors();
        return panel;
    }

    private class ConnectorNotSelectedException extends Exception {

        private static final long serialVersionUID = 1L;

        public ConnectorNotSelectedException(String string) {
            super(string);
        }
    }
}
