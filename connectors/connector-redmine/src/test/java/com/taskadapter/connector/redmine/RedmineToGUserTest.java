package com.taskadapter.connector.redmine;

import com.taskadapter.redmineapi.bean.User;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RedmineToGUserTest {
    @Test
    public void loginIsConverted() {
        User redmineUser = new User(null);
        redmineUser.setLogin("mylogin");
        assertEquals("mylogin", RedmineToGUser.convertToGUser(redmineUser).getLoginName());
    }

    @Test
    public void idIsConverted() {
        User redmineUser = new User(null).setId(33);
        assertTrue(RedmineToGUser.convertToGUser(redmineUser).getId() == 33);
    }

    @Test
    public void fullNameIsConverted() {
        User redmineUser = new User(null);
        redmineUser.setFullName("full name");
        assertEquals("full name", RedmineToGUser.convertToGUser(redmineUser).getDisplayName());
    }
}
