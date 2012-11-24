package com.taskadapter.web.configeditor;

import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.vaadin.data.Property;
import com.vaadin.ui.Panel;

public class ServerPanel extends Panel implements Validatable {
    private static final String SERVER_GROUP_LABEL = "Server info";
    private final ServerInfoCache cache;
    private ServerContainer serverContainer;

    public ServerPanel(ServerInfoCache cache, Property labelProperty, Property serverURLProperty, Property userLoginNameProperty,
                       Property passwordProperty) {
        this.cache = cache;
        buildUI(labelProperty, serverURLProperty, userLoginNameProperty, passwordProperty);
    }

    private void buildUI(Property labelProperty, Property serverURLProperty, Property userLoginNameProperty,
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
