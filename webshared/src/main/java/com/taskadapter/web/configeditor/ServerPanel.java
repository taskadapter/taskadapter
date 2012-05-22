package com.taskadapter.web.configeditor;

import com.taskadapter.connector.definition.ValidationException;
import com.taskadapter.connector.definition.WebServerInfo;
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

    /**
     * Config Editors should NOT create this object directly, use ConfigEditor.addServerPanel() method instead.
     *
     * ASK: Presently this panel is not used in all editors(eg Redmine, MSP),
     *      so handling code should be removed from ServerPanel
     *
     * @see ConfigEditor#addServerPanel()
     */
    public ServerPanel() {
        buildUI();
    }

    private void buildUI() {
        addStyleName("panelexample");
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
        layout.addComponent(hostURLText, 1, 0);
        layout.setComponentAlignment(hostURLText, Alignment.MIDDLE_RIGHT);

        Label loginLabel = new Label("Login:");
        layout.addComponent(loginLabel, 0, 1);
        layout.setComponentAlignment(loginLabel, Alignment.MIDDLE_LEFT);

        login = new TextField();
        login.addStyleName("server-panel-textfield");
        layout.addComponent(login, 1, 1);
        layout.setComponentAlignment(login, Alignment.MIDDLE_RIGHT);

        Label pswdLabel = new Label("Password:");
        layout.addComponent(pswdLabel, 0, 2);
        layout.setComponentAlignment(pswdLabel, Alignment.MIDDLE_LEFT);

        password = new PasswordField();
        password.addStyleName("server-panel-textfield");
        layout.addComponent(password, 1, 2);
        layout.setComponentAlignment(password, Alignment.MIDDLE_RIGHT);
    }

    private void cleanup() {
        if (getHostString().endsWith("/")) {
            hostURLText.setValue(getHostString().substring(0, getHostString().length() - 1));
        }
    }

    private String getHostString() {
        return (String) hostURLText.getValue();
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

    public void setServerInfo(WebServerInfo info) {
        EditorUtil.setNullSafe(hostURLText, info.getHost());
        EditorUtil.setNullSafe(login, info.getUserName());
        EditorUtil.setNullSafe(password, info.getPassword());
    }

    public void disableServerURLField() {
        hostURLText.setEnabled(false);
    }
}
