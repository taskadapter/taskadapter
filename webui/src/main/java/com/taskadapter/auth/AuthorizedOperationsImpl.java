package com.taskadapter.auth;

/** Default implementation of authorization scheme. */
public final class AuthorizedOperationsImpl implements AuthorizedOperations {
    public static final String ADMIN_LOGIN_NAME = "admin";

    /** Information about current user. */
    private final String user;

    public AuthorizedOperationsImpl(String user) {
        this.user = user;
    }

    /**
     * Checks, if user is "ADMIN" user. When user is not authorized, returns
     * false.
     */
    private boolean isAdmin() {
        return ADMIN_LOGIN_NAME.equals(user);
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
        return isAdmin();
    }

    @Override
    public boolean canConfigureServer() {
        return isAdmin();
    }

    @Override
    public boolean canAddUsers() {
        return isAdmin();
    }

}
