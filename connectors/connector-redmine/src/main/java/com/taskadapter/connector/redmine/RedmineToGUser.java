package com.taskadapter.connector.redmine;

import com.taskadapter.model.GUser;
import com.taskadapter.redmineapi.bean.User;

public class RedmineToGUser {
    public static GUser convertToGUser(User redmineUser) {
        return new GUser(redmineUser.getId(), redmineUser.getLogin(), redmineUser.getFullName());
    }
}
