package com.taskadapter.web.configeditor.server;

import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.vaadin14shim.PasswordField;
import com.taskadapter.vaadin14shim.TextField;
import com.taskadapter.web.configeditor.EditorUtil;

public class ServerPanelUtil {
    public static TextField label(WebConnectorSetup setup) {
        return EditorUtil.textField(setup, "label");
    }

    public static TextField userName(WebConnectorSetup setup) {
        return EditorUtil.textField(setup, "userName");
    }

    public static TextField host(WebConnectorSetup setup) {
        return EditorUtil.textField(setup, "host");
    }

    public static PasswordField password(WebConnectorSetup setup) {
        return EditorUtil.passwordField(setup, "password");
    }

    public static PasswordField apiKey(WebConnectorSetup setup) {
        return EditorUtil.passwordField(setup, "apiKey");
    }
}
