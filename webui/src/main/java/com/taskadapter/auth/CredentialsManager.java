package com.taskadapter.auth;

import java.util.List;

/**
 * User credentails manager. Manages user primary/secondary credentials.
 */
public interface CredentialsManager {
    /**
     * Checks, if a user "secondary-auth" key is authentic to user.
     * 
     * @param user
     *            user.
     * @param auth
     *            secondary authentication token.
     * @return user autorhized operations or <code>null</code> if user is not
     *         authorized.
     */
    AuthorizedOperations authenticateSecondary(String user, String auth);

    /**
     * Checks, if a user is authentic using a primary authentication token.
     * 
     * @param user
     *            user to check.
     * @param auth
     *            authentication token to use.
     * @return user autorhized operations or <code>null</code> if user is not
     *         authorized.
     */
    AuthorizedOperations authenticatePrimary(String user, String auth);

    /**
     * Generates a secondary authentication key for a primary authentication
     * token. If primary authentication is invalid, returns null. Otherwise
     * returns a secondary authentication token bound to a primary token.
     * 
     * @param user
     *            user name.
     * @param primaryAuth
     *            user primary authentication token.
     * @return <code>null</code> of primary authentication is invalid or
     *         secondary authentication token otherwhise.
     * @throws AuthException
     *             if credentials are valid, but new token cannot be generated.
     */
    SecondarizationResult generateSecondaryAuth(String user,
                                                String primaryAuth) throws AuthException;

    /**
     * Deletes a secondary authentication token. After call to this method,
     * {@link #authenticateSecondary(String, String)} must return false to this
     * token.
     * 
     * @param user
     *            user name to remove token.
     * @param secondaryAuth
     *            secondary auth token to remove.
     * @throws AuthException
     *             if token cannot be removed (but was found as authentic).
     */
    void destroySecondaryAuthToken(String user, String secondaryAuth)
            throws AuthException;

    /**
     * Sets a new primary authentication token for a user. Does not check for an
     * old auth and allows to "reset" any user password. Resets ALL secondary
     * authentication tokens bound to a user.
     * <p>
     * The reason to reset secondary auths is possible primary auth compromise.
     * When hacker receives a primary auth token, she will be able to create a
     * secondary token and use then later. Thus, user must be able to reset all
     * secondary tokens everywhere. Changing primary credentials will require
     * all "saved" sessions to re-authenticate, thus restoring user identity.
     * <p>
     * This method may be called to set an auth for non-existing user.
     * 
     * @param user
     *            user to set a primary token for.
     * @param newToken
     *            new authentication token (password).
     * @throws AuthException
     *             if token cannot be updated.
     */
    void savePrimaryAuthToken(String user, String newToken)
            throws AuthException;

    /**
     * Destroys all secondary tokens for a user. See "Logout everywhere" button
     * on stackoverflow.com. Main reason for this function is to destroy all
     * sessions in unknown state and cause possible attackers to reauthenticate.
     * Session loss may occurs in public places, electricity powerdowns, etc...
     * Secondary tokens may be reset only for an existing user.
     * 
     * @param user
     *            user to destroy a tokens for.
     * @throws AuthException
     *             if some error occured during a reset.
     */
    void destroyAllSecondaryTokens(String user) throws AuthException;

    /**
     * Lists all existing users.
     * 
     * @return list of all existing users.
     */
    List<String> listUsers();

    /**
     * Removes user.
     * 
     * @param user
     *            user name.
     * @throws AuthException
     *             if user exists and cannot be deleted.
     */
    void removeUser(String user) throws AuthException;
}
