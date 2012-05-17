package com.taskadapter.web.service;

public class User {
    private String loginName;
    private String password;

    public User(String loginName, String password) {
        this.loginName = loginName;
        this.password = password;
    }

    public User() {
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
