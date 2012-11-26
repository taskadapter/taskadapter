package com.taskadapter.web.configeditor.server;

import java.util.List;

import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.web.configeditor.DefaultPanel;
import com.taskadapter.web.configeditor.Validatable;
import com.vaadin.data.Property;
import com.vaadin.ui.Panel;

public class ServerPanel extends Panel implements Validatable {
    private static final String SERVER_GROUP_LABEL = "Server info";
    private ServerContainer serverContainer;

    public ServerPanel(List<WebServerInfo> cache, Property labelProperty, Property serverURLProperty, Property userLoginNameProperty,
                       Property passwordProperty) {
        buildUI(cache, labelProperty, serverURLProperty, userLoginNameProperty, passwordProperty);
    }

    private void buildUI(List<WebServerInfo> cache, Property labelProperty, Property serverURLProperty, Property userLoginNameProperty,
                         Property passwordProperty) {
        setWidth(DefaultPanel.NARROW_PANEL_WIDTH);
        serverContainer = new ServerContainer(cache, labelProperty, serverURLProperty, userLoginNameProperty, passwordProperty);

        addComponent(serverContainer);
        setCaption(SERVER_GROUP_LABEL);
    }

    @Override
    public void validate() throws ServerURLNotSetException {
        if (serverContainer.getHostString().isEmpty()) {
            throw new ServerURLNotSetException();
        }
    }

    /**
     * @deprecated use "read-only" host property.
     */
//    @Deprecated
//    public void disableServerURLField() {
//        serverContainer.getServerURLField().setEnabled(false);
//    }

}
