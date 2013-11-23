package com.taskadapter.web.configeditor.server;

import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.web.configeditor.DefaultPanel;
import com.taskadapter.web.configeditor.Validatable;
import com.vaadin.data.Property;
import com.vaadin.ui.Panel;

public class ServerPanel extends Panel implements Validatable {
    private static final String SERVER_GROUP_LABEL = "Server info";
    private ServerContainer serverContainer;

    public ServerPanel(Property<String> labelProperty, Property<String> serverURLProperty, Property<String> userLoginNameProperty,
                       Property<String> passwordProperty) {
        buildUI(labelProperty, serverURLProperty, userLoginNameProperty, passwordProperty);
    }

    private void buildUI(Property<String> labelProperty, Property<String> serverURLProperty, Property<String> userLoginNameProperty,
                         Property<String> passwordProperty) {
        setWidth(DefaultPanel.NARROW_PANEL_WIDTH);
        serverContainer = new ServerContainer(labelProperty, serverURLProperty, userLoginNameProperty, passwordProperty);

        setContent(serverContainer);
        setCaption(SERVER_GROUP_LABEL);
    }

    @Override
    public void validate() throws ServerURLNotSetException {
        if (serverContainer.getHostString().isEmpty()) {
            throw new ServerURLNotSetException();
        }
    }
}
