package com.taskadapter.connector.basecamp;

import com.taskadapter.model.GUser;

final class DirectUserResolver implements UserResolver {

    @Override
    public GUser resolveUser(GUser user) {
        return user;
    }

}
