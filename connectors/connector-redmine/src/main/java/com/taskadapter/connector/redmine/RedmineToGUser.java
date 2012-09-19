package com.taskadapter.connector.redmine;

import com.taskadapter.model.GUser;
import com.taskadapter.redmineapi.bean.User;

public class RedmineToGUser {
    public static GUser convertToGUser(User redmineUser) {
        GUser user = new GUser();
        user.setId(redmineUser.getId());
        user.setLoginName(redmineUser.getLogin());
        user.setDisplayName(redmineUser.getFullName());
        return user;
    }
}
