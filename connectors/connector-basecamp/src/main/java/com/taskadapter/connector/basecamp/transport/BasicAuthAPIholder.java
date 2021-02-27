package com.taskadapter.connector.basecamp.transport;

import com.taskadapter.connector.definition.WebConnectorSetup;

public class BasicAuthAPIholder extends ObjectAPIHolder {

    private final String login;
    private final String password;

    public BasicAuthAPIholder(ObjectAPI api, String userId,
                              WebConnectorSetup setup) {
        super(api, userId);
        this.login = setup.getUserName();
        this.password = setup.getPassword();
    }

    @Override
    boolean accepts(WebConnectorSetup setup) {
        return login.equals(setup.getUserName())
                && password.equals(setup.getPassword());
    }

}
