package com.taskadapter.webui;

import com.vaadin.ui.LoginForm;
import com.vaadin.ui.VerticalLayout;

public class LoginPage extends Page {

    private Authenticator authenticator;
    private PageManager pageManager;

    public LoginPage(Authenticator authenticator, PageManager pageManager) {
        this.authenticator = authenticator;
        this.pageManager = pageManager;
        buildUI();
    }

    private void buildUI() {
        VerticalLayout layout = new VerticalLayout();
        LoginForm loginForm = new LoginForm();
        loginForm.addListener(new LoginForm.LoginListener() {
            @Override
            public void onLogin(LoginForm.LoginEvent event) {
                String username = event.getLoginParameter("username");
                String password = event.getLoginParameter("password");
                authenticator.tryLogin(username, password);
                if (authenticator.isLoggedIn()) {
                    pageManager.show(new HomePage(authenticator));
                }
            }
        });
        layout.addComponent(loginForm);
        setCompositionRoot(layout);
    }

    @Override
    public String getPageTitle() {
        return "Login";
    }
}
