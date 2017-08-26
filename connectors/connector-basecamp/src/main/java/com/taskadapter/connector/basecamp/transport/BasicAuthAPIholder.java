package com.taskadapter.connector.basecamp.transport;

import com.taskadapter.connector.definition.WebConnectorSetup;

public class BasicAuthAPIholder extends ObjectAPIHolder {

    private final String login;
    private final String password;

    public BasicAuthAPIholder(ObjectAPI api, String userId,
                              WebConnectorSetup setup) {
        super(api, userId);
        this.login = setup.userName();
        this.password = setup.password();
    }

    @Override
    boolean accepts(WebConnectorSetup setup) {
        return login.equals(setup.userName())
                && password.equals(setup.password());
    }

}
