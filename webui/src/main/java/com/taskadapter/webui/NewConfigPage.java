package com.taskadapter.webui;

import com.taskadapter.config.ConnectorDataHolder;
import com.taskadapter.config.TAFile;
import com.taskadapter.connector.definition.Descriptor;
import com.vaadin.data.Validator;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.*;

import java.util.Iterator;

/**
 * @author Alexey Skorokhodov
 */
public class NewConfigPage extends Page {
    private static final String NAME_HINT = "My tasks";

    private TextField name;
    private ListSelect connector1;
    private ListSelect connector2;
    private Form form;
    private TAFile newFile;

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
        name.setRequiredError("Please provide a config name");
        name.setInputPrompt(NAME_HINT);
        name.setWidth("100%");

        grid.addComponent(name, 0, 0, 1, 0);
        grid.setComponentAlignment(name, Alignment.MIDDLE_CENTER);

        connector1 = new ListSelect("Connector 1");
        connector1.setRequired(true);
        connector1.setRequiredError("Connector 1 is not selected");
        connector1.setNullSelectionAllowed(false);
        grid.addComponent(connector1, 0, 1);

        connector2 = new ListSelect("Connector 2");
        connector2.setRequired(true);
        connector2.setRequiredError("Connector 2 is not selected");
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
        form.getFooter().addComponent(saveButton);
    }

    private void loadDataConnectors() {
        Iterator<Descriptor> connectors = services.getPluginManager().getPluginDescriptors();
        while (connectors.hasNext()) {
            Descriptor connector = connectors.next();
            connector1.addItem(connector.getLabel());
            connector2.addItem(connector.getLabel());
        }
    }

    private void saveClicked() {
        try {
            validate();
            form.setComponentError(null);
            save();
            showTaskDetailsPage();

            name.setValue("");   //clear name for new config
        } catch (Validator.InvalidValueException e) {
            form.setComponentError(new UserError(e.getMessage()));
        }
    }

    private void showTaskDetailsPage() {
        navigator.showTaskDetailsPage(newFile);
    }

    private void validate() {
        name.validate();
        connector1.validate();
        connector2.validate();
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
