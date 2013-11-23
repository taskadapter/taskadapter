package com.taskadapter.webui;

import com.taskadapter.auth.AuthorizedOperations;

/**
 * Context for logged-in user.
 */
public final class UserContext {
    /**
     * User name.
     */
    public final String name;

    /**
     * Self-management facilities.
     */
    public final SelfManagement selfManagement;

    /**
     * User permissions.
     */
    public final AuthorizedOperations authorizedOps;

    /**
     * Operations on the sync configuration.
     */
    public final ConfigOperations configOps;

    public UserContext(String name, SelfManagement selfManagement,
            AuthorizedOperations authorizedOps, ConfigOperations configOps) {
        this.name = name;
        this.selfManagement = selfManagement;
        this.authorizedOps = authorizedOps;
        this.configOps = configOps;
    }

}
