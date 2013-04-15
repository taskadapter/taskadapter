package com.taskadapter.connector.basecamp.classic;

import com.taskadapter.model.GUser;

public interface UserResolver {
    /**
     * Resolves a user by a user. Converts user into a Basecamp user. May return
     * <code>null</code> if no user found. May receive null as input.
     * 
     * @param user
     *            user to resolve.
     * @return resolved user.
     */
    public GUser resolveUser(GUser user);
}
