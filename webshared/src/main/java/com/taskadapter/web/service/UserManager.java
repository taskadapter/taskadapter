package com.taskadapter.web.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private Map<String, User> usersMap;

    // TODO use any persistent storage
    public UserManager() {
        usersMap = new HashMap<String, User>();
        // TODO encrypt
        User user1 = new User("admin", "admin");
        usersMap.put(user1.getLoginName(), user1);
    }

    public Collection<User> getUsers() {
        return usersMap.values();
    }

    public void createUser(String loginName) {
        User user = new User();
        user.setLoginName(loginName);
        usersMap.put(loginName, user);
    }

    public void deleteUser(String loginName) {
        usersMap.remove(loginName);
    }

    public void setPassword(String loginName, String newPassword) {
        User user = usersMap.get(loginName);
        if (user == null) {
            throw new RuntimeException("User not found: " + loginName);
        }
        user.setPassword(newPassword);
    }
}
