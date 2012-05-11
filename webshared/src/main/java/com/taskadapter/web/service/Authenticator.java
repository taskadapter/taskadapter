package com.taskadapter.web.service;

import java.util.Calendar;

public class Authenticator {
    private boolean loggedIn;
    private String userName;

    private CookiesManager cookiesManager;

    public Authenticator(CookiesManager cookiesManager) {
        this.cookiesManager = cookiesManager;
    }

    public void init() {
        this.loggedIn = Boolean.parseBoolean(cookiesManager.getCookie("loggedIn"));
        this.userName = cookiesManager.getCookie("userName");
    }

    public void tryLogin(String userName, String password, boolean staySigned) {
        // TODO implement some simple authentication
        this.loggedIn = true;
        this.userName = userName;
        if (userName.equals("admin") && password.equals("admin")) {
            this.loggedIn = true;
            this.userName = userName;

            if (staySigned) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, 1);

                cookiesManager.setCookie("loggedIn", "true", cal.getTime());
                cookiesManager.setCookie("userName", userName, cal.getTime());
            }
        }
    }

    public void logout() {
        this.loggedIn = false;
        this.userName = "";

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);

        cookiesManager.setCookie("loggedIn", "", cal.getTime());
        cookiesManager.setCookie("userName", "", cal.getTime());
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public String getUserName() {
        return userName;
    }
}
