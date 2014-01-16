package com.taskadapter.auth;

/**
 * Result of creating secondary auth token.
 */
public final class SecondarizationResult {
    /** 
     * Operations authorized for the user.
     */
    public final AuthorizedOperations ops;
    
    /**
     * Secondary access token.
     */
    public final String secondaryToken;

    public SecondarizationResult(AuthorizedOperations ops, String secondaryToken) {
        this.ops = ops;
        this.secondaryToken = secondaryToken;
    }

}
