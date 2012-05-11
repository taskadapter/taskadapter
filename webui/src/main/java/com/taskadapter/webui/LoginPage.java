package com.taskadapter.webui;

import com.vaadin.ui.*;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.CheckBox;

import javax.swing.plaf.ButtonUI;
import java.util.Calendar;
import java.util.Date;

public class LoginPage extends Page {
    private VerticalLayout layout = new VerticalLayout();
    private com.vaadin.ui.CheckBox staySigned;
    private Label lbl = new Label("[cookies not read]", Label.CONTENT_XHTML);

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
                services.getAuthenticator().tryLogin(username, password, staySigned.booleanValue());
                if (services.getAuthenticator().isLoggedIn()) {
                    navigator.show(Navigator.HOME);
                }
            }
        });
        layout.addComponent(loginForm);

        staySigned = new com.vaadin.ui.CheckBox("Stay signed");
        layout.addComponent(staySigned);

        Button btn = new Button("Show");
        btn.addListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                String txt = "";
                for (String name : services.getCookiesManager().getCookiesComponent().getCookieNames()) {
                    txt += name + " = '" + services.getCookiesManager().getCookiesComponent().getCookie(name) + "'<br />";
                }

                lbl.setCaption(txt);
            }
        });
        layout.addComponent(btn);

        layout.addComponent(lbl);
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
