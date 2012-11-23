package com.taskadapter.auth.cred;

/**
 * Zero version of credentials.
 *
 */
public final class CredentialsV0 extends Credentials {
    
    /**
     * User password.
     */
    public final String password;

    public CredentialsV0(String password) {
        this.password = password;
    }

}
