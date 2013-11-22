package com.taskadapter.webui.service;

import com.taskadapter.auth.AuthorizedOperations;
import com.taskadapter.web.SettingsManager;

/** Default implementation of authorization scheme. */
public final class DefaultAutorizedOps implements AuthorizedOperations {
    public static final String ADMIN_LOGIN_NAME = "admin";

    /** Information about current user. */
    private final CurrentUserInfo userInfo;
    /** Application-wide settings. */
    private final SettingsManager settingsManager;

    DefaultAutorizedOps(CurrentUserInfo userInfo,
            SettingsManager settingsManager) {
        super();
        this.userInfo = userInfo;
        this.settingsManager = settingsManager;
    }

    /**
     * Checks, if user is "ADMIN" user. When user is not authorized, returns
     * false.
     */
    private boolean isAdmin() {
        return userInfo.isLoggedIn()
                && ADMIN_LOGIN_NAME.equals(userInfo.getUserName());
    }

    @Override
    public boolean canAcceptLicense() {
        return isAdmin();
    }

    @Override
    public boolean canChangePasswordFor(String otherUser) {
        return isAdmin();
    }

    @Override
    public boolean canDeleteUser(String userToDelete) {
        return isAdmin() && !ADMIN_LOGIN_NAME.equals(userToDelete);
    }

    @Override
    public boolean canManagerPeerConfigs() {
        return isAdmin() && settingsManager.adminCanManageAllConfigs();
    }

    @Override
    public boolean canChangeServerSettings() {
        return isAdmin();
    }

}
