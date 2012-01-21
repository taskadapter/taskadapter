package com.taskadapter.web.configeditor;

import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.definition.WebServerInfo;
import com.vaadin.event.FieldEvents;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

/**
 * @author Alexey Skorokhodov
 */
public class ServerPanel extends GridLayout implements Validatable {
    private static final String HOST_URL_TOOLTIP = "Host URL, including protocol prefix and port number. E.g. http://demo.site.com:3000";

    private static final String SERVER_GROUP_LABEL = "Server info";

    private TextField hostURLText;
    private TextField login;
    private PasswordField password;

    /**
     * Config Editors should NOT create this object directly, use ConfigEditor.addServerPanel() method instead.
     * @see ConfigEditor#addServerPanel()
     */
    ServerPanel() {
        init();
    }

    private void init() {
        setCaption(SERVER_GROUP_LABEL);
        setColumns(2);

		addComponent(new Label("Server URL:"));
		hostURLText = new TextField();
        hostURLText.setDescription(HOST_URL_TOOLTIP);
		hostURLText.addListener(new FieldEvents.BlurListener() {
            @Override
            public void blur(FieldEvents.BlurEvent event) {
                cleanup();
            }
        });
        addComponent(hostURLText);

        addComponent(new Label("Login:"));
		login = new TextField();
        addComponent(login);
        
        addComponent(new Label("Password:"));
		password = new PasswordField();
        addComponent(password);
    }

    private void cleanup() {
        if (getHostString().endsWith("/")) {
            hostURLText.setValue(getHostString().substring(0, getHostString().length() - 1));
        }
    }

    private String getHostString() {
        return (String) hostURLText.getValue();
    }

    @Override
    public void validate() throws ValidationException {
        if (getHostString().isEmpty()) {
            throw new ValidationException("'Redmine URL' is not set");
        }
    }

    public WebServerInfo getServerInfo() {
        return new WebServerInfo(getHostString(), (String) login.getValue(), (String) password.getValue());
    }

    public void setServerInfo(WebServerInfo info) {
        EditorUtil.setNullSafe(hostURLText, info.getHost());
        EditorUtil.setNullSafe(login, info.getUserName());
        EditorUtil.setNullSafe(password, info.getPassword());
    }
}
