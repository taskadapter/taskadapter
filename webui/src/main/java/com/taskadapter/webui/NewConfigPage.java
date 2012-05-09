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
    private static final String NAME_HINT = "Enter config name";
    private static final String NAME_REQUIRED_MESSAGE = "Please provide the config name";
    private static final String SELECT_CONNECTOR_1_MESSAGE = "Please select " + ConfigsPage.SYSTEM_1_TITLE;
    private static final String SELECT_CONNECTOR_2_MESSAGE = "Please select " + ConfigsPage.SYSTEM_2_TITLE;

    private TextField name;
    private ListSelect connector1;
    private ListSelect connector2;
    private Form form;
    private TAFile newFile;
    private Label errorMessageLabel;

    public NewConfigPage() {
        buildUI();
    }

    private void buildUI() {
        form = new Form();
        form.setSizeFull();

        GridLayout grid = new GridLayout(2, 2);
        grid.setSpacing(true);

        name = new TextField("Config name");
        name.setRequired(true);
        name.setInputPrompt(NAME_HINT);
        name.setWidth("100%");

        grid.addComponent(name, 0, 0, 1, 0);
        grid.setComponentAlignment(name, Alignment.MIDDLE_CENTER);

        connector1 = new ListSelect(ConfigsPage.SYSTEM_1_TITLE);
        connector1.setRequired(true);
        connector1.setNullSelectionAllowed(false);
        grid.addComponent(connector1, 0, 1);

        connector2 = new ListSelect(ConfigsPage.SYSTEM_2_TITLE);
        connector2.setRequired(true);
        connector2.setNullSelectionAllowed(false);
        grid.addComponent(connector2, 1, 1);

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
    }

    private void saveClicked() {
        try {
            validate();
            errorMessageLabel.setValue("");
            save();
            showTaskDetailsPage();

            name.setValue("");   //clear name for new config
        } catch (ValidationException e) {
            errorMessageLabel.setValue(e.getMessage());
        }
    }

    private void showTaskDetailsPage() {
        navigator.showConfigureTaskPage(newFile);
    }

    private void validate() throws ValidationException {
        if (name.getValue().equals("")) {
            name.setRequiredError(NAME_REQUIRED_MESSAGE);
            throw new ValidationException(NAME_REQUIRED_MESSAGE);
        } else {
            name.setRequiredError("");
        }

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
        String nameStr = (String) name.getValue();
        String id1 = (String) connector1.getValue();
        String id2 = (String) connector2.getValue();

        Descriptor descriptor1 = services.getPluginManager().getDescriptor(id1);
        ConnectorDataHolder d1 = new ConnectorDataHolder(id1,
                // TODO replace with factory.createDefaultConfig()
                descriptor1.createDefaultConfig());
        Descriptor descriptor2 = services.getPluginManager().getDescriptor(id2);
        ConnectorDataHolder d2 = new ConnectorDataHolder(id2,
                descriptor2.createDefaultConfig());
        this.newFile = new TAFile(nameStr, d1, d2);
        services.getConfigStorage().saveConfig(newFile);
    }

    @Override
    public String getPageTitle() {
        return "New synchronization config";
    }

    @Override
    public Component getUI() {
        loadDataConnectors();
        return form;
    }
}
