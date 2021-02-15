package com.taskadapter.webui.auth;

import com.taskadapter.webui.SessionController;
import com.taskadapter.webui.WebUserSession;
import org.junit.Test;

public class PermissionsTest {
    @Test
    public void adminHasAdminPermission() {
        var session = new WebUserSession().setCurrentUserName("admin");
        SessionController.initSession(session);
        Permissions.check(Permissions.ADMIN);
    }

    @Test(expected = PermissionViolationException.class)
    public void nonAdminUserHasNoAdminPermission() {
        var session = new WebUserSession().setCurrentUserName("user");
        SessionController.initSession(session);
        Permissions.check(Permissions.ADMIN);
    }

    @Test(expected = PermissionViolationException.class)
    public void notLoggedInUserHasNoLoginPermission() {
        var session = new WebUserSession();
        SessionController.initSession(session);
        Permissions.check(Permissions.LOGGED_IN);
    }
}