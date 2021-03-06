package com.taskadapter.connector.basecamp;

import com.taskadapter.connector.basecamp.transport.ObjectAPI;
import com.taskadapter.connector.definition.WebConnectorSetup;

class TestBasecampConfig {
    private static final String accountId = "4039521";
    private static final String USER_LOGIN = "d42dse+81ho6e4c19m44@sharklasers.com";
    private static final String USER_PASSWORD = "1234567f";
    private static final String PROJECT_KEY = "15524708";

    static BasecampConfig config() {
        BasecampConfig config = new BasecampConfig();
        config.setAccountId(accountId);
        config.setProjectKey(PROJECT_KEY);
        config.setFindUserByName(true);
        return config;
    }

    static WebConnectorSetup setup() {
        return WebConnectorSetup.apply(BasecampConnector.ID, "label",
                ObjectAPI.BASECAMP_URL,
                USER_LOGIN, USER_PASSWORD, false, "");
    }
}
