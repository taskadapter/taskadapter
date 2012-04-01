package com.taskadapter.webui;

public class Authenticator {
    private boolean loggedIn;
    private String userName;

    public void tryLogin(String userName, String password) {
        // TODO implement some simple authentication
        this.loggedIn = true;
        this.userName = "admin";
//        if (userName.equals("admin") && password.equals("admin")) {
//            this.loggedIn = true;
//            this.userName = userName;
//        }
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public String getUserName() {
        return userName;
    }
}
