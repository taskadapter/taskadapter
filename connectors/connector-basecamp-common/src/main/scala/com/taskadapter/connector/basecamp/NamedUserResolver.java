package com.taskadapter.connector.basecamp;

import java.util.Map;

import com.taskadapter.model.GUser;

public class NamedUserResolver implements UserResolver {
    private final Map<String, GUser> users;

    public NamedUserResolver(Map<String, GUser> users) {
        this.users = users;
    }

    @Override
    public GUser resolveUser(GUser user) {
        if (user == null) {
            return user;
        }
        final GUser guess1 = users.get(user.loginName());
        if (guess1 != null) {
            return guess1;
        }
        return users.get(user.displayName());
    }

}
