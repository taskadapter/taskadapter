package com.taskadapter.web.service;

import com.taskadapter.config.User;

public class Authenticator {
    private static final String LOGGED_IN_COOKIE_NAME = "loggedIn";
    private static final String USER_NAME_COOKIE_NAME = "userName";

    private boolean loggedIn;
    private String userName;
    private UserManager userManager;
    private CookiesManager cookiesManager;

    public Authenticator(UserManager userManager, CookiesManager cookiesManager) {
        this.userManager = userManager;
        this.cookiesManager = cookiesManager;
    }

    public void init() {
        this.loggedIn = Boolean.parseBoolean(cookiesManager.getCookie(LOGGED_IN_COOKIE_NAME));
        this.userName = cookiesManager.getCookie(USER_NAME_COOKIE_NAME);
    }

    public void tryLogin(String userName, String password, boolean staySigned) throws UserNotFoundException, WrongPasswordException {
        User actualUser = userManager.getUser(userName);
        if (!password.equals(actualUser.getPassword())) {
            throw new WrongPasswordException();
        }

        this.loggedIn = true;
        this.userName = userName;

        if (staySigned) {
            setLoggedInCookieFor1Month(userName);
        }
    }

    private void setLoggedInCookieFor1Month(String userName) {
        cookiesManager.setCookie(LOGGED_IN_COOKIE_NAME, "true");
        cookiesManager.setCookie(USER_NAME_COOKIE_NAME, userName);
    }

    public void logout() {
        this.loggedIn = false;
        this.userName = "";

        cookiesManager.expireCookie(LOGGED_IN_COOKIE_NAME);
        cookiesManager.expireCookie(USER_NAME_COOKIE_NAME);
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public String getUserName() {
        return userName;
    }
}
