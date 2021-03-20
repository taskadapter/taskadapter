package com.taskadapter.connector.redmine;

import com.taskadapter.redmineapi.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Resolves users by either login name or full name. limitation of Redmine REST API...
 */
public class RedmineUserCache {
    private static final Logger logger = LoggerFactory.getLogger(RedmineUserCache.class);

    private List<User> users;

    public RedmineUserCache(List<User> users) {
        this.users = users;
    }

    Optional<User> findRedmineUserInCache(Integer id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    Optional<User> findRedmineUserByLogin(String loginName) {
        return users.stream()
                .filter(user -> user.getLogin().equals(loginName))
                .findFirst();
    }

    Optional<User> findRedmineUserByFullName(String fullName) {
        return users.stream()
                .filter(user -> user.getFullName().equals(fullName))
                .findFirst();
    }
}
