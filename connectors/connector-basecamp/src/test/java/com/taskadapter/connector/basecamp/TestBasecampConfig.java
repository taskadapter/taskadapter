package com.taskadapter.connector.basecamp;

class TestBasecampConfig {
    public static final String USER_FIRST_NAME = "Tester";

    private static final String USER_ID = "2439494";
    private static final String USER_LOGIN = "ipkwfgrx@sharklasers.com";
    private static final String USER_PASSWORD = "123123123";
    private static final String PROJECT_KEY = "4189112";

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
