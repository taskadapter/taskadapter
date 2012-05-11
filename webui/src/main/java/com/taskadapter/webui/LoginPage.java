package com.taskadapter.webui;

import com.vaadin.ui.*;

import javax.swing.plaf.ButtonUI;
import java.util.Calendar;
import java.util.Date;

public class LoginPage extends Page {
    private VerticalLayout layout = new VerticalLayout();
    private Label lbl = new Label("[cookies not read]", Label.CONTENT_RAW);

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

        Button btn = new Button("Set");
        btn.addListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                services.getCookiesManager().setCookie("loggedin", "true", new Date());
            }
        });
        layout.addComponent(btn);

        btn = new Button("Expire");
        btn.addListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, -1);
                services.getCookiesManager().setCookie("loggedin", "true", cal.getTime());
            }
        });
        layout.addComponent(btn);

        btn = new Button("Show");
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
