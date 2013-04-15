package com.taskadapter.connector.basecamp.classic;

import com.taskadapter.model.GUser;

final class DirectUserResolver implements UserResolver {

    @Override
    public GUser resolveUser(GUser user) {
        return user;
    }

}
