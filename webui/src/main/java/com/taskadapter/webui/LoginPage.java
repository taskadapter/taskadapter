package com.taskadapter.webui;

import com.taskadapter.web.service.LoginException;
import com.vaadin.ui.*;

public class LoginPage extends Page {
    private VerticalLayout layout = new VerticalLayout();
    private TextField loginEdit;
    private PasswordField passwordEdit;
    private Label errorLabel;

    private CheckBox staySignedIn;

    public LoginPage() {
        buildUI();
    }

    private void clearLoginFields() {
        loginEdit.setValue("");
        passwordEdit.setValue("");
        staySignedIn.setValue(false);
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
        loginButton.addListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                String username = (String) loginEdit.getValue();
                String password = (String) passwordEdit.getValue();
                try {
                    services.getAuthenticator().tryLogin(username, password, staySignedIn.booleanValue());
                    clearLoginFields();
                    errorLabel.setValue("");
                    if (services.getAuthenticator().isLoggedIn()) {
                        navigator.updateLogoutButtonState();
                        navigator.show(Navigator.HOME);
                    }
                } catch (LoginException e) {
                    errorLabel.setValue(e.getMessage());
                }
            }
        });
        layout.addComponent(loginButton);

        errorLabel = new Label();
        errorLabel.addStyleName("errorMessage");
        layout.addComponent(errorLabel);

        clearLoginFields();
    }

    @Override
    public String getPageTitle() {
        return "Login";
    }

    @Override
    public Component getUI() {
        return layout;
    }
}
