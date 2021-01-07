package com.taskadapter.web.configeditor.server;

import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.vaadin14shim.Binder;
import com.taskadapter.vaadin14shim.TextField;
import com.taskadapter.vaadin14shim.PasswordField;
import com.taskadapter.vaadin14shim.GridLayout;
import com.taskadapter.webui.Page;
import com.vaadin.event.FieldEvents;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;

public class ServerContainer extends GridLayout {
    private static final String DEFAULT_HOST_VALUE = "http://";

    private TextField hostURLText;

    public ServerContainer(WebConnectorSetup setup) {
        buildUI(setup);
    }

    private void buildUI(WebConnectorSetup setup) {
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
        nameField.addClassName("server-panel-textfield");
        Binder.bindField(nameField, setup, "label");
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
        hostURLText.addClassName("server-panel-textfield");
        Binder.bindField(hostURLText, setup, "host");

        addComponent(hostURLText, 1, currentRow);
        setComponentAlignment(hostURLText, Alignment.MIDDLE_RIGHT);

        currentRow++;

        Label loginLabel = new Label(Page.message("setupPanel.login"));
        addComponent(loginLabel, 0, currentRow);
        setComponentAlignment(loginLabel, Alignment.MIDDLE_LEFT);

        TextField login = new TextField();
        login.addClassName("server-panel-textfield");
        Binder.bindField(login, setup, "userName");
        addComponent(login, 1, currentRow);
        setComponentAlignment(login, Alignment.MIDDLE_RIGHT);

        currentRow++;

        Label pswdLabel = new Label(Page.message("setupPanel.password"));
        addComponent(pswdLabel, 0, currentRow);
        setComponentAlignment(pswdLabel, Alignment.MIDDLE_LEFT);

        PasswordField password = new PasswordField();
        password.addClassName("server-panel-textfield");
        Binder.bindField(password, setup, "password");
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
