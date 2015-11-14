package com.taskadapter.auth;

/**
 * Interface which allows an application to query about supported operations.
 */
public interface AuthorizedOperations {
    /** Checks, if current user can accept a license. */
    boolean canAcceptLicense();

    /** Checks, if user can manage passwords for other user. */
    boolean canChangePasswordFor(String otherUser);

    /** Checks, if this user can delete other user. */
    boolean canDeleteUser(String userToDelete);

    /** Checks, if user can see and manage configs for other users. */
    boolean canManagerPeerConfigs();

    /**
     * Checks, if user can configure server.
     */
    boolean canConfigureServer();
}
