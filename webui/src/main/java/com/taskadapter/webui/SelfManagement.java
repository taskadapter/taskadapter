package com.taskadapter.webui;

import com.taskadapter.auth.AuthException;
import com.taskadapter.auth.CredentialsManager;
import com.taskadapter.webui.service.WrongPasswordException;

/**
 * User self-management services.
 */
public class SelfManagement {

    /**
     * User login.
     */
    private final String login;

    /**
     * Credentials manager.
     */
    private final CredentialsManager credentialManager;

    /**
     * Creates a new self-manager.
     * 
     * @param login
     *            user login.
     * @param credentialManager
     *            used credentials manager.
     */
    SelfManagement(String login, CredentialsManager credentialManager) {
        this.login = login;
        this.credentialManager = credentialManager;
    }

    /**
     * Attempts to change user password.
     * 
     * @param oldPassword
     *            old user password.
     * @param newPassword
     *            new user password.
     * @throws AuthException
     *             if password cannot be changed.
     * @throws WrongPasswordException
     *             if password was wrong.
     */
    public void changePassword(String oldPassword, String newPassword)
            throws AuthException, WrongPasswordException {
        if (credentialManager.authenticatePrimary(login, oldPassword) == null) {
            throw new WrongPasswordException();
        }
        credentialManager.savePrimaryAuthToken(login, newPassword);

    }
}
