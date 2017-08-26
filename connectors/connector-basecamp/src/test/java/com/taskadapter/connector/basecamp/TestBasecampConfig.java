package com.taskadapter.connector.basecamp;

import com.taskadapter.connector.basecamp.transport.ObjectAPI;
import com.taskadapter.connector.definition.WebConnectorSetup;
import scala.Option;

class TestBasecampConfig {
    static final String USER_FIRST_NAME = "Tester";

    private static final String USER_ID = "3826992";
    private static final String USER_LOGIN = "sufis@storj99.top";
    private static final String USER_PASSWORD = "65690812487";
    private static final String PROJECT_KEY = "4715519";

    static BasecampConfig config() {
        BasecampConfig config = new BasecampConfig();
        config.setAccountId(USER_ID);
        config.setProjectKey(PROJECT_KEY);
        config.setLookupUsersByName(true);
        return config;
    }

    static WebConnectorSetup setup() {
        return new WebConnectorSetup(BasecampConnector.ID(), Option.empty(), "label",
                ObjectAPI.BASECAMP_URL,
                USER_LOGIN, USER_PASSWORD, false, "");
    }
}
