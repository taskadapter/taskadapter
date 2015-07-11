package com.taskadapter.connector.redmine;

import com.taskadapter.redmineapi.bean.User;
import com.taskadapter.redmineapi.bean.UserFactory;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class RedmineToGUserTest {
    @Test
    public void loginIsConverted() {
        User redmineUser = UserFactory.create();
        redmineUser.setLogin("mylogin");
        assertEquals("mylogin", RedmineToGUser.convertToGUser(redmineUser).getLoginName());
    }

    @Test
    public void idIsConverted() {
        User redmineUser = UserFactory.create(33);
        assertEquals((Integer) 33, RedmineToGUser.convertToGUser(redmineUser).getId());
    }

    @Test
    public void fullNameIsConverted() {
        User redmineUser = UserFactory.create();
        redmineUser.setFullName("full name");
        assertEquals("full name", RedmineToGUser.convertToGUser(redmineUser).getDisplayName());
    }
}
