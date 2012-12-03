package com.taskadapter.connector.basecamp.transport;

import com.taskadapter.connector.basecamp.BasecampAuth;
import com.taskadapter.connector.basecamp.BasicBasecampAuth;

public class BasicAuthAPIholder extends ObjectAPIHolder {

    private final String login;
    private final String password;

    public BasicAuthAPIholder(ObjectAPI api, String userId,
            BasicBasecampAuth auth) {
        super(api, userId);
        this.login = auth.getLogin();
        this.password = auth.getPassword();
    }

    @Override
    boolean accepts(BasecampAuth auth) {
        if (!(auth instanceof BasicBasecampAuth)) {
            return false;
        }
        final BasicBasecampAuth bba = (BasicBasecampAuth) auth;
        return login.equals(bba.getLogin())
                && password.equals(bba.getPassword());
    }

}
