package com.taskadapter.connector.basecamp;

class TestBasecampConfig {
    public static final String USER_FIRST_NAME = "Tester";

    private static final String USER_ID = "2584780";
    private static final String USER_LOGIN = "cwg0l20+2q3h7g@sharklasers.com";
    private static final String USER_PASSWORD = "123123123";
    private static final String PROJECT_KEY = "5375264";

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
