package com.taskadapter.connector.basecamp;

class TestBasecampConfig {
    public static final String USER_FIRST_NAME = "Tester";

    private static final String USER_ID = "2515207";
    private static final String USER_LOGIN = "dgkcc9q+luqv9c@sharklasers.com";
    private static final String USER_PASSWORD = "123123123";
    private static final String PROJECT_KEY = "4768037";

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
