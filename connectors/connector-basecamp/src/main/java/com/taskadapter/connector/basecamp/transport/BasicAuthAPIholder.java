package com.taskadapter.connector.basecamp.transport;

import com.taskadapter.connector.basecamp.BasecampAuth;

public class BasicAuthAPIholder extends ObjectAPIHolder {

    private final String login;
    private final String password;

    public BasicAuthAPIholder(ObjectAPI api, String userId,
                              BasecampAuth auth) {
        super(api, userId);
        this.login = auth.getLogin();
        this.password = auth.getPassword();
    }

    @Override
    boolean accepts(BasecampAuth auth) {
        return login.equals(auth.getLogin())
                && password.equals(auth.getPassword());
    }

}
