package com.taskadapter.connector.basecamp.classic;

class TestBasecampConfig {
    private static final String PROJECT_KEY = "12955684-testproject";

    static BasecampConfig create() {
        BasecampConfig config = new BasecampConfig();
        config.setServerUrl("https://altadev.basecamphq.com");
        config.setApiKey("ba1bf0af26c0f1e55f92aac5c2447a1576a398cd");
        config.setProjectKey(PROJECT_KEY);
//        config.setLookupUsersByName(true);
        return config;
    }
}
