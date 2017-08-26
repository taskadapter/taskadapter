package com.taskadapter.connector.basecamp;

import com.taskadapter.connector.basecamp.transport.ObjectAPI;
import com.taskadapter.connector.definition.WebConnectorSetup;
import scala.Option;

class TestBasecampConfig {
    private static final String USER_ID = "3827020";
    private static final String USER_LOGIN = "sufis@storj99.top";
    private static final String USER_PASSWORD = "65690812487";
    private static final String PROJECT_KEY = "14455873";

    static BasecampConfig config() {
        BasecampConfig config = new BasecampConfig();
        config.setAccountId(USER_ID);
        config.setProjectKey(PROJECT_KEY);
        config.setFindUserByName(true);
        return config;
    }

    static WebConnectorSetup setup() {
        return new WebConnectorSetup(BasecampConnector.ID(), Option.empty(), "label",
                ObjectAPI.BASECAMP_URL,
                USER_LOGIN, USER_PASSWORD, false, "");
    }
}
