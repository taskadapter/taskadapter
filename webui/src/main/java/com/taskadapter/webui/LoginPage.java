package com.taskadapter.webui;

import com.vaadin.ui.*;

public class LoginPage extends Page {
    private VerticalLayout layout = new VerticalLayout();
    private CheckBox staySignedIn;
    private Label debugCookiesLabel = new Label("", Label.CONTENT_XHTML);

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
                services.getAuthenticator().tryLogin(username, password, staySignedIn.booleanValue());
                if (services.getAuthenticator().isLoggedIn()) {
                    navigator.show(Navigator.HOME);
                }
            }
        });
        layout.addComponent(loginForm);


        staySignedIn = new com.vaadin.ui.CheckBox("Stay signed in");
        layout.addComponent(staySignedIn);

        Button btn = new Button("DEBUG");
        btn.addListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                String txt = "";
                for (String name : services.getCookiesManager().getCookiesComponent().getCookieNames()) {
                    txt += name + " = '" + services.getCookiesManager().getCookiesComponent().getCookie(name) + "'<br />";
                }

                debugCookiesLabel.setCaption(txt);
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
