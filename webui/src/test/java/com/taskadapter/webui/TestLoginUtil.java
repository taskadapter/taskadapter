package com.taskadapter.webui;

public class TestLoginUtil {
    public static void loginAsAdmin() {
        SessionController.initSession(new WebUserSession().setCurrentUserName("admin"));
    }
}
