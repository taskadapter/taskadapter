package com.taskadapter.web.configeditor;

import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.definition.WebServerInfo;
import com.vaadin.event.FieldEvents;
import com.vaadin.ui.*;

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
     *
     * @see ConfigEditor#addServerPanel()
     */
    ServerPanel() {
        init();
    }

    private void init() {
        addStyleName("bordered_panel");
        setCaption(SERVER_GROUP_LABEL);
        setColumns(2);
        setRows(3);
        setMargin(true);
        setSpacing(true);

        Label urlLabel = new Label("Server URL:");
        addComponent(urlLabel, 0, 0);
        setComponentAlignment(urlLabel, Alignment.MIDDLE_LEFT);

        hostURLText = new TextField();
        hostURLText.setDescription(HOST_URL_TOOLTIP);
        hostURLText.addListener(new FieldEvents.BlurListener() {
            @Override
            public void blur(FieldEvents.BlurEvent event) {
                cleanup();
            }
        });
        hostURLText.setWidth("212px");
        addComponent(hostURLText, 1, 0);
        setComponentAlignment(hostURLText, Alignment.MIDDLE_RIGHT);

        Label logiLabel = new Label("Login:");
        addComponent(logiLabel, 0, 1);
        setComponentAlignment(logiLabel, Alignment.MIDDLE_LEFT);

        login = new TextField();
        login.setWidth("212px");
        addComponent(login, 1, 1);
        setComponentAlignment(login, Alignment.MIDDLE_RIGHT);

        Label pswdLabel = new Label("Password:");
        addComponent(pswdLabel, 0, 2);
        setComponentAlignment(pswdLabel, Alignment.MIDDLE_LEFT);

        password = new PasswordField();
        password.setWidth("212px");
        addComponent(password, 1, 2);
        setComponentAlignment(password, Alignment.MIDDLE_RIGHT);
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
            throw new ValidationException("'Server URL' is not set");
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
