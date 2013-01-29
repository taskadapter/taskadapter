package com.taskadapter.webui;

import com.taskadapter.webui.service.WrongPasswordException;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Runo;

public class LoginPage extends Page {
    private TextField loginEdit;
    private PasswordField passwordEdit;
    private Label errorLabel;
    private static final String LOG_IN_TITLE = "Log in";
    private static final String LOGIN_PROMPT = "Login";
    private static final String PASSWORD_PROMPT = "Password";
    private static final String STAY_SIGNED_IN = "Stay signed in";
    private static final String HINT_LABEL = "<i>Default login/password: admin/admin</i>";
    private static final String FORM_WIDTH = "300px";
    private static final String EDIT_WIDTH = "260px";

    private CheckBox staySignedIn;
    private static final boolean DEFAULT_STAY_SIGNED_IN_CHECKBOX_STATE = true;
    private Panel panel = new Panel(LOG_IN_TITLE);

    public LoginPage() {
        buildUI();
    }

    private void clearLoginFields() {
        loginEdit.setValue("");
        passwordEdit.setValue("");
        staySignedIn.setValue(DEFAULT_STAY_SIGNED_IN_CHECKBOX_STATE);
    }

    private void buildUI() {
        final VerticalLayout layout = new VerticalLayout();
        panel.addComponent(layout);
        layout.setWidth(FORM_WIDTH);

        layout.setMargin(false, true, false, true);
        layout.setSpacing(true);
        final Label label = new Label(HINT_LABEL, Label.CONTENT_XHTML);
        label.setStyleName(Runo.LABEL_SMALL);
        layout.addComponent(label);

        loginEdit = new TextField();
        loginEdit.setCaption(LOGIN_PROMPT);
        loginEdit.setWidth(EDIT_WIDTH);
        layout.addComponent(loginEdit);

        passwordEdit = new PasswordField();
        passwordEdit.setCaption(PASSWORD_PROMPT);
        passwordEdit.setWidth(EDIT_WIDTH);
        layout.addComponent(passwordEdit);

        staySignedIn = new CheckBox(STAY_SIGNED_IN);
        layout.addComponent(staySignedIn);

        Button loginButton = new Button(LOG_IN_TITLE);
        loginButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        loginButton.addStyleName(Runo.BUTTON_DEFAULT);
        loginButton.addStyleName(Runo.BUTTON_BIG);

        loginButton.addListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                String username = (String) loginEdit.getValue();
                String password = (String) passwordEdit.getValue();
                try {
                    navigator.login(username, password, staySignedIn.booleanValue());
                    clearLoginFields();
                    errorLabel.setValue("");
                } catch (WrongPasswordException e) {
                    errorLabel.setValue("Wrong user name or password.");
                    passwordEdit.setValue("");
                    passwordEdit.focus();
                }
            }
        });
        layout.addComponent(loginButton);

        errorLabel = new Label();
        errorLabel.addStyleName("errorMessage");
        layout.addComponent(errorLabel);

        clearLoginFields();
        loginEdit.focus();
    }

    @Override
    public String getPageGoogleAnalyticsID() {
        return "login";
    }

    @Override
    public Component getUI() {
        return panel;
    }
}
