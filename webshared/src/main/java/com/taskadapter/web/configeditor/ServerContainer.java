package com.taskadapter.web.configeditor;

import com.taskadapter.connector.definition.WebConfig;
import com.taskadapter.connector.definition.WebServerInfo;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.event.FieldEvents;
import com.vaadin.ui.*;

public class ServerContainer extends GridLayout {
    private static final String HOST_URL_TOOLTIP = "Host URL, including protocol prefix and port number. E.g. http://demo.site.com:3000";
    private static final String DEFAULT_HOST_VALUE = "http://";

    private TextField descriptionField;
    private TextField hostURLText;

    public ServerContainer(WebConfig config) {
        buildUI(config);
    }

    private void buildUI(WebConfig config) {
        setColumns(2);
        setRows(4);
        setMargin(true);
        setSpacing(true);

        int currentRow = 0;
        Label descriptionLabel = new Label("Description:");
        addComponent(descriptionLabel, 0, currentRow);
        setComponentAlignment(descriptionLabel, Alignment.MIDDLE_LEFT);
        descriptionField = new TextField();
        descriptionField.addStyleName("server-panel-textfield");
        descriptionField.setPropertyDataSource(new MethodProperty<String>(config, "label"));
        addComponent(descriptionField, 1, currentRow);

        currentRow++;

        Label urlLabel = new Label("Server URL:");
        addComponent(urlLabel, 0, currentRow);
        setComponentAlignment(urlLabel, Alignment.MIDDLE_LEFT);

        hostURLText = new TextField();
        hostURLText.setDescription(HOST_URL_TOOLTIP);
        hostURLText.addListener(new FieldEvents.BlurListener() {
            @Override
            public void blur(FieldEvents.BlurEvent event) {
                //TODO refactor these methods (common in ServerPanel and RedmineServerPanel
                checkProtocol();
                cleanup();
            }
        });
        hostURLText.addStyleName("server-panel-textfield");

        WebServerInfo webServerInfo = config.getServerInfo();
        hostURLText.setPropertyDataSource(new MethodProperty<String>(webServerInfo, "host"));

        addComponent(hostURLText, 1, currentRow);
        setComponentAlignment(hostURLText, Alignment.MIDDLE_RIGHT);

        currentRow++;

        Label loginLabel = new Label("Login:");
        addComponent(loginLabel, 0, currentRow);
        setComponentAlignment(loginLabel, Alignment.MIDDLE_LEFT);

        TextField login = new TextField();
        login.addStyleName("server-panel-textfield");
        login.setPropertyDataSource(new MethodProperty<String>(webServerInfo, "userName"));
        addComponent(login, 1, currentRow);
        setComponentAlignment(login, Alignment.MIDDLE_RIGHT);

        currentRow++;

        Label pswdLabel = new Label("Password:");
        addComponent(pswdLabel, 0, currentRow);
        setComponentAlignment(pswdLabel, Alignment.MIDDLE_LEFT);

        PasswordField password = new PasswordField();
        password.addStyleName("server-panel-textfield");
        password.setPropertyDataSource(new MethodProperty<String>(webServerInfo, "password"));
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

    TextField getServerURLField() {
        return hostURLText;
    }

    String getHostString() {
        return (String) hostURLText.getValue();
    }

}
