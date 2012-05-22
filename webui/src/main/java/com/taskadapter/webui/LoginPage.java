package com.taskadapter.webui;

import com.taskadapter.web.service.UserNotFoundException;
import com.taskadapter.web.service.WrongPasswordException;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;

public class LoginPage extends Page {
    private VerticalLayout layout = new VerticalLayout();
    private TextField loginEdit;
    private PasswordField passwordEdit;
    private Label errorLabel;

    private CheckBox staySignedIn;
    private static final boolean DEFAULT_STAY_SIGNED_IN_CHECKBOX_STATE = true;

    public LoginPage() {
        buildUI();
    }

    private void clearLoginFields() {
        loginEdit.setValue("");
        passwordEdit.setValue("");
        staySignedIn.setValue(DEFAULT_STAY_SIGNED_IN_CHECKBOX_STATE);
    }

    private void buildUI() {
        loginEdit = new TextField();
        loginEdit.setInputPrompt("Username");
        loginEdit.setCaption("Username");
        layout.addComponent(loginEdit);

        passwordEdit = new PasswordField();
        passwordEdit.setInputPrompt("*********");
        passwordEdit.setCaption("Password");
        layout.addComponent(passwordEdit);

        staySignedIn = new CheckBox("Stay signed in");
        layout.addComponent(staySignedIn);

        Button loginButton = new Button("Login");
        loginButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        loginButton.addStyleName("v-button-default");

        loginButton.addListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                String username = (String) loginEdit.getValue();
                String password = (String) passwordEdit.getValue();
                try {
                    services.getAuthenticator().tryLogin(username, password, staySignedIn.booleanValue());
                    clearLoginFields();
                    errorLabel.setValue("");
                    navigator.show(Navigator.HOME);
                } catch (WrongPasswordException e) {
                    errorLabel.setValue("Wrong password.");
                    passwordEdit.setValue("");
                    passwordEdit.focus();
                } catch (UserNotFoundException e) {
                    errorLabel.setValue("User " + username + " not found.");
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
        return layout;
    }
}
