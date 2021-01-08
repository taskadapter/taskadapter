package com.taskadapter.webui.auth;

import com.taskadapter.webui.SessionController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class Permissions {

    public static final String LOGGED_IN = "Logged in";
    public static final String ADMIN = "Logged in";

    public static void check(final String permission) {
        if (!has(permission)) {
            throw new PermissionViolationException(permission);
        }
    }

    public static boolean has(final String permission) {
        return getPermissionsForCurrentUser().contains(permission);
    }

    /**
     * @return never null. empty list when no user is logged in
     */
    private static Collection<String> getPermissionsForCurrentUser() {
        if (!SessionController.userIsLoggedIn()) {
            return new ArrayList<>();
        }

        List<String> list = new ArrayList<>();
        list.add(LOGGED_IN);
        if (SessionController.getCurrentUserName().equals("admin")) {
            list.add(ADMIN);
        }
        return list;
    }
}
