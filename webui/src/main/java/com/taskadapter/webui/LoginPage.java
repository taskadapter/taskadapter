package com.taskadapter.webui;

import com.vaadin.ui.Component;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.VerticalLayout;

public class LoginPage extends Page {
    private VerticalLayout layout = new VerticalLayout();

    public LoginPage() {
        buildUI();
    }

    private void buildUI() {
        LoginForm loginForm = new LoginForm();
        loginForm.addListener(new LoginForm.LoginListener() {
            @Override
            public void onLogin(LoginForm.LoginEvent event) {
                String username = event.getLoginParameter("username");
                String password = event.getLoginParameter("password");
                services.getAuthenticator().tryLogin(username, password);
                if (services.getAuthenticator().isLoggedIn()) {
                    navigator.show(Navigator.HOME);
                }
            }
        });
        layout.addComponent(loginForm);
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
