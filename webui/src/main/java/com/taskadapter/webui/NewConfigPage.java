package com.taskadapter.webui;

import com.taskadapter.config.ConnectorDataHolder;
import com.taskadapter.config.TAFile;
import com.taskadapter.connector.definition.Descriptor;
import com.taskadapter.connector.definition.ValidationException;
import com.vaadin.ui.*;

import java.util.Iterator;

/**
 * @author Alexey Skorokhodov
 */
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
    private TAFile newFile;
    private Label errorMessageLabel;

    public NewConfigPage() {
        buildUI();
    }

    private void buildUI() {
        panel = new Panel("Create new config");
        panel.setWidth("600px");
        Form form = new Form();
        panel.addComponent(form);
        //form.setSizeFull();

        GridLayout grid = new GridLayout(2, 2);
        grid.setSpacing(true);

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

        Button saveButton = new Button("Create");
        saveButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                saveClicked();
            }
        });

        form.setLayout(grid);

        // empty label by default
        errorMessageLabel = new Label();
        errorMessageLabel.addStyleName("error-message-label");

        VerticalLayout bottomPanel = new VerticalLayout();
        bottomPanel.addComponent(saveButton);
        bottomPanel.addComponent(errorMessageLabel);
        form.getFooter().addComponent(bottomPanel);
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
            save();
            showTaskDetailsPage();

            //clear for new config
            descriptionTextField.setValue("");
        } catch (ValidationException e) {
            errorMessageLabel.setValue(e.getMessage());
        }
    }

    private void showTaskDetailsPage() {
        navigator.showConfigureTaskPage(newFile);
    }

    private void validate() throws ValidationException {
        if (connector1.getValue() == null) {
            connector1.setRequiredError(SELECT_CONNECTOR_1_MESSAGE);
            throw new ValidationException(SELECT_CONNECTOR_1_MESSAGE);
        } else {
            connector1.setRequiredError("");
        }

        if (connector2.getValue() == null) {
            connector1.setRequiredError(SELECT_CONNECTOR_2_MESSAGE);
            throw new ValidationException(SELECT_CONNECTOR_2_MESSAGE);
        } else {
            connector2.setRequiredError("");
        }
    }

    private void save() {
        String descriptionString = (String) descriptionTextField.getValue();
        String id1 = (String) connector1.getValue();
        String id2 = (String) connector2.getValue();

        Descriptor descriptor1 = services.getPluginManager().getDescriptor(id1);
        ConnectorDataHolder d1 = new ConnectorDataHolder(id1,
                // TODO replace with factory.createDefaultConfig()
                descriptor1.createDefaultConfig());
        Descriptor descriptor2 = services.getPluginManager().getDescriptor(id2);
        ConnectorDataHolder d2 = new ConnectorDataHolder(id2,
                descriptor2.createDefaultConfig());
        this.newFile = new TAFile(descriptionString, d1, d2);
        String currentUserLoginName = services.getAuthenticator().getUserName();
        services.getConfigStorage().createNewConfig(currentUserLoginName, newFile);
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
}
