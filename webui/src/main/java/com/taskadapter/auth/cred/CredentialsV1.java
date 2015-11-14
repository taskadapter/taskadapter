package com.taskadapter.auth.cred;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * More "advanced" version of user credentials.
 */
public final class CredentialsV1 extends Credentials {

    /**
     * Primary user credentials.
     */
    public final String primaryCredentials;

    /**
     * Secondary user credentials. This list is immutable.
     */
    public final List<String> secondaryCredentials;

    public CredentialsV1(String primaryCredentials,
            List<String> secondaryCredentials) {
        this.primaryCredentials = primaryCredentials;
        this.secondaryCredentials = Collections
                .unmodifiableList(new ArrayList<>(secondaryCredentials));
    }

}
