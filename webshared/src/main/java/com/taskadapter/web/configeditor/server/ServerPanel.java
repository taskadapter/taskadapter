package com.taskadapter.web.configeditor.server;

import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.web.configeditor.DefaultPanel;
import com.taskadapter.web.configeditor.Validatable;
import com.vaadin.data.Property;
import com.vaadin.ui.Panel;

public class ServerPanel extends Panel implements Validatable {
    private ServerContainer serverContainer;

    public ServerPanel(String caption, Property<String> labelProperty, Property<String> serverURLProperty, Property<String> userLoginNameProperty,
                       Property<String> passwordProperty) {
        buildUI(caption, labelProperty, serverURLProperty, userLoginNameProperty, passwordProperty);
    }

    private void buildUI(String caption, Property<String> nameProperty, Property<String> serverURLProperty, Property<String> userLoginNameProperty,
                         Property<String> passwordProperty) {
        setWidth(DefaultPanel.NARROW_PANEL_WIDTH);
        serverContainer = new ServerContainer(nameProperty, serverURLProperty, userLoginNameProperty, passwordProperty);

        setContent(serverContainer);
        setCaption(caption);
    }

    @Override
    public void validate() throws BadConfigException {
        if (serverContainer.getHostString().isEmpty()) {
            throw new ServerURLNotSetException();
        }
    }
}
