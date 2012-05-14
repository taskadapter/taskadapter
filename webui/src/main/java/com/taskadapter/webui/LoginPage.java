package com.taskadapter.webui;

import com.google.gwt.editor.client.Editor;
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

    private void buildUI() {
/*        LoginForm loginForm = new LoginForm();
        loginForm.addListener(new LoginForm.LoginListener() {
            @Override
            public void onLogin(LoginForm.LoginEvent event) {
                String username = event.getLoginParameter("username");
                String password = event.getLoginParameter("password");
                services.getAuthenticator().tryLogin(username, password, staySignedIn.booleanValue());
                if (services.getAuthenticator().isLoggedIn()) {
                    navigator.show(Navigator.HOME);
                }
            }
        });
        layout.addComponent(loginForm);*/

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

        Button btn = new Button("DEBUG");
        btn.addListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                debugCookiesLabel.setCaption(services.getCookiesManager().getCookies());
            }
        });
        layout.addComponent(btn);

        btn = new Button("LOGIN");
        btn.addListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                String username = (String) loginEdit.getValue();
                String password = (String) passwordEdit.getValue();
                services.getAuthenticator().tryLogin(username, password, staySignedIn.booleanValue());
                if (services.getAuthenticator().isLoggedIn()) {
                    navigator.show(Navigator.HOME);
                }
            }
        });
        layout.addComponent(btn);

        btn = new Button("LOGOUT");
        btn.addListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                services.getAuthenticator().logout();
                navigator.show(Navigator.HOME);
            }
        });
        layout.addComponent(btn);

        layout.addComponent(debugCookiesLabel);
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
