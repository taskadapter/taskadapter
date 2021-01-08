package com.taskadapter.web.configeditor.server;

import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.web.configeditor.EditorUtil;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

public class ServerPanelUtil {
    public static TextField label(Binder<WebConnectorSetup> binder) {
        return EditorUtil.textInput(binder, "label");
    }

    public static TextField userName(Binder<WebConnectorSetup> binder) {
        return EditorUtil.textInput(binder, "userName");
    }

    public static TextField host(Binder<WebConnectorSetup> binder) {
        return EditorUtil.textInput(binder, "host");
    }

    public static PasswordField password(Binder<WebConnectorSetup> binder) {
        return EditorUtil.passwordInput(binder, "password");
    }

    public static PasswordField apiKey(Binder<WebConnectorSetup> binder) {
        return EditorUtil.passwordInput(binder, "apiKey");
    }
}
