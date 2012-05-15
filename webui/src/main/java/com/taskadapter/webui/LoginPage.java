package com.taskadapter.webui;

import com.vaadin.ui.*;

public class LoginPage extends Page {
    private VerticalLayout layout = new VerticalLayout();
    private TextField loginEdit;
    private PasswordField passwordEdit;

    private CheckBox staySignedIn;
    private Label debugCookiesLabel = new Label("", Label.CONTENT_XHTML);

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
                services.getAuthenticator().tryLogin(username, password, staySignedIn.booleanValue());
                clearLoginFields();
                if (services.getAuthenticator().isLoggedIn()) {
                    navigator.updateLogoutButtonState();
                    navigator.show(Navigator.HOME);
                }
            }
        });
        layout.addComponent(loginButton);

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
