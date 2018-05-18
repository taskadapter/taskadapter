package com.taskadapter.connector.redmine;

import com.taskadapter.redmineapi.bean.User;
import com.taskadapter.redmineapi.bean.UserFactory;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RedmineToGUserTest {
    @Test
    public void loginIsConverted() {
        User redmineUser = UserFactory.create();
        redmineUser.setLogin("mylogin");
        assertEquals("mylogin", RedmineToGUser.convertToGUser(redmineUser).loginName());
    }

    @Test
    public void idIsConverted() {
        User redmineUser = UserFactory.create(33);
        assertTrue(RedmineToGUser.convertToGUser(redmineUser).id() == 33);
    }

    @Test
    public void fullNameIsConverted() {
        User redmineUser = UserFactory.create();
        redmineUser.setFullName("full name");
        assertEquals("full name", RedmineToGUser.convertToGUser(redmineUser).displayName());
    }
}
