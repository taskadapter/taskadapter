package com.taskadapter.connector.basecamp;

class TestBasecampConfig {
    private static final String USER_ID = "2260133";
    private static final String USER_LOGIN = "rtfxmqks@sharklasers.com";
    private static final String USER_PASSWORD = "123123123";
    private static final String PROJECT_KEY = "2834112";

    static BasecampConfig create() {
        BasecampConfig config = new BasecampConfig();
        final BasecampAuth auth = new BasecampAuth();
        auth.setLogin(USER_LOGIN);
        auth.setPassword(USER_PASSWORD);
        auth.setUseAPIKeyInsteadOfLoginPassword(false);
        config.setAuth(auth);
        config.setAccountId(USER_ID);
        config.setProjectKey(PROJECT_KEY);
        config.setLookupUsersByName(true);
        return config;
    }
}
