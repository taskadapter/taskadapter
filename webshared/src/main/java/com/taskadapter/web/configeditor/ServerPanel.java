package com.taskadapter.web.configeditor;

import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.definition.WebServerInfo;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.event.FieldEvents;
import com.vaadin.ui.*;

/**
 * @author Alexey Skorokhodov
 */
public class ServerPanel extends Panel implements Validatable {
    private static final String HOST_URL_TOOLTIP = "Host URL, including protocol prefix and port number. E.g. http://demo.site.com:3000";
    private static final String DEFAULT_HOST_VALUE = "http://";
    private static final String SERVER_GROUP_LABEL = "Server info";

    private TextField hostURLText;
    private TextField login;
    private PasswordField password;
    private final WebServerInfo config;

    public ServerPanel(WebServerInfo config) {
    	this.config = config;
        buildUI();
    }

    private void buildUI() {
        setWidth(DefaultPanel.NARROW_PANEL_WIDTH);
        GridLayout layout = new GridLayout();
        addComponent(layout);
        setCaption(SERVER_GROUP_LABEL);
        layout.setColumns(2);
        layout.setRows(3);
        layout.setMargin(true);
        layout.setSpacing(true);

        Label urlLabel = new Label("Server URL:");
        layout.addComponent(urlLabel, 0, 0);
        layout.setComponentAlignment(urlLabel, Alignment.MIDDLE_LEFT);

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
		hostURLText.setPropertyDataSource(new MethodProperty<String>(config,
				"host"));
        
        layout.addComponent(hostURLText, 1, 0);
        layout.setComponentAlignment(hostURLText, Alignment.MIDDLE_RIGHT);

        Label loginLabel = new Label("Login:");
        layout.addComponent(loginLabel, 0, 1);
        layout.setComponentAlignment(loginLabel, Alignment.MIDDLE_LEFT);

        login = new TextField();
        login.addStyleName("server-panel-textfield");
		login.setPropertyDataSource(new MethodProperty<String>(config,
				"userName"));
        layout.addComponent(login, 1, 1);
        layout.setComponentAlignment(login, Alignment.MIDDLE_RIGHT);

        Label pswdLabel = new Label("Password:");
        layout.addComponent(pswdLabel, 0, 2);
        layout.setComponentAlignment(pswdLabel, Alignment.MIDDLE_LEFT);

        password = new PasswordField();
        password.addStyleName("server-panel-textfield");
		password.setPropertyDataSource(new MethodProperty<String>(config,
				"password"));
        layout.addComponent(password, 1, 2);
        layout.setComponentAlignment(password, Alignment.MIDDLE_RIGHT);
    }

    private void cleanup() {
        if (getHostString().endsWith("/")) {
            hostURLText.setValue(getHostString().substring(0, getHostString().length() - 1));
        }
    }

    private String getHostString() {
        return config.getHost();
    }

    private void checkProtocol() {
        if (!getHostString().startsWith("http")) {
            hostURLText.setValue(DEFAULT_HOST_VALUE + hostURLText.getValue());
        }
    }

    @Override
    public void validate() throws ValidationException {
        if (getHostString().isEmpty()) {
            throw new ValidationException("Server URL is not set");
        }
    }

    public WebServerInfo getServerInfo() {
        return new WebServerInfo(getHostString(), (String) login.getValue(), (String) password.getValue());
    }

    public void disableServerURLField() {
        hostURLText.setEnabled(false);
    }

}
