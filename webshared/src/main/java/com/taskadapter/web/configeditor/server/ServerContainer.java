package com.taskadapter.web.configeditor.server;

import com.taskadapter.webui.Page;
import com.vaadin.data.Property;
import com.vaadin.event.FieldEvents;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

public class ServerContainer extends GridLayout {
    private static final String DEFAULT_HOST_VALUE = "http://";

    private TextField hostURLText;

    public ServerContainer(Property<String> nameProperty,
            Property<String> serverURLProperty,
            Property<String> userLoginNameProperty,
            Property<String> passwordProperty) {
        buildUI(nameProperty, serverURLProperty, userLoginNameProperty,
                passwordProperty);
    }

    private void buildUI(Property<String> labelProperty,
            Property<String> serverURLProperty,
            Property<String> userLoginNameProperty, Property<String> passwordProperty) {
        setColumns(2);
        setRows(4);
        setMargin(true);
        setSpacing(true);

        int currentRow = 0;
        Label nameLabel = new Label(Page.message("setupPanel.name"));
        addComponent(nameLabel, 0, currentRow);
        setComponentAlignment(nameLabel, Alignment.MIDDLE_LEFT);
        TextField nameField = new TextField();
        nameField.setRequired(true);
        nameField.addStyleName("server-panel-textfield");
        nameField.setPropertyDataSource(labelProperty);
        addComponent(nameField, 1, currentRow);

        currentRow++;

        Label urlLabel = new Label(Page.message("setupPanel.serverUrl"));
        addComponent(urlLabel, 0, currentRow);
        setComponentAlignment(urlLabel, Alignment.MIDDLE_LEFT);

        hostURLText = new TextField();
        hostURLText.setDescription(Page.message("setupPanel.serverUrlHint"));
        hostURLText.addBlurListener((FieldEvents.BlurListener) event -> {
            //TODO refactor these methods (common in ServerPanel and RedmineServerPanel
            checkProtocol();
            cleanup();
        });
        hostURLText.addStyleName("server-panel-textfield");
        hostURLText.setPropertyDataSource(serverURLProperty);

        addComponent(hostURLText, 1, currentRow);
        setComponentAlignment(hostURLText, Alignment.MIDDLE_RIGHT);

        currentRow++;

        Label loginLabel = new Label(Page.message("setupPanel.login"));
        addComponent(loginLabel, 0, currentRow);
        setComponentAlignment(loginLabel, Alignment.MIDDLE_LEFT);

        TextField login = new TextField();
        login.addStyleName("server-panel-textfield");
        login.setPropertyDataSource(userLoginNameProperty);
        addComponent(login, 1, currentRow);
        setComponentAlignment(login, Alignment.MIDDLE_RIGHT);

        currentRow++;

        Label pswdLabel = new Label(Page.message("setupPanel.password"));
        addComponent(pswdLabel, 0, currentRow);
        setComponentAlignment(pswdLabel, Alignment.MIDDLE_LEFT);

        PasswordField password = new PasswordField();
        password.addStyleName("server-panel-textfield");
        password.setPropertyDataSource(passwordProperty);
        addComponent(password, 1, currentRow);
        setComponentAlignment(password, Alignment.MIDDLE_RIGHT);
    }

    private void cleanup() {
        if (getHostString().endsWith("/")) {
            hostURLText.setValue(getHostString().substring(0, getHostString().length() - 1));
        }
    }

    private void checkProtocol() {
        if (!getHostString().startsWith("http")) {
            hostURLText.setValue(DEFAULT_HOST_VALUE + hostURLText.getValue());
        }
    }

    private String getHostString() {
        return hostURLText.getValue();
    }

}
