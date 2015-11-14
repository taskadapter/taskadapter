package com.taskadapter.auth.cred;

import java.util.List;

import com.taskadapter.auth.AuthException;

/**
 * Store for a user credentials information.
 * 
 */
public interface CredentialsStore {
    /**
     * Loads a credentials for a specified user. Never returns <code>null</code>
     * , must throw {@link AuthException} if there are no user credentials..
     * 
     * @param user
     *            user to get a credentials for.
     * @return user credentials data, never <code>null</code>.
     * @throws AuthException
     *             if credentials cannot be found.
     */
    Credentials loadCredentials(String user) throws AuthException;

    /**
     * Saves a new user credentials. Only last credentials version is supported.
     * 
     * @param user
     *            user name to change a credentials for.
     * @param credentials
     *            credentials to save.
     * @throws AuthException
     *             if credentials cannot be saved.
     */
    void saveCredentials(String user, CredentialsV1 credentials)
            throws AuthException;

    /**
     * Lists all users.
     * 
     * @return list of found users.
     */
    List<String> listUsers();

    /**
     * Removes user.
     * @param user user to remove.
     * @throws AuthException if user exists and cannot be deleted.
     */
    void removeUser(String user) throws AuthException;
}
