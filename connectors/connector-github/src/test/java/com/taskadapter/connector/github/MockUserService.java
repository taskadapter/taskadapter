package com.taskadapter.connector.github;

import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.UserService;

public class MockUserService extends UserService {
    @Override
    public User getUser(String login) {
        return new User()
                .setLogin(login)
                .setName("name");
    }
}
