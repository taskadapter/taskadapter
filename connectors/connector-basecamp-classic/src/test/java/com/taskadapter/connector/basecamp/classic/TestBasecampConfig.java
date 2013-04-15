package com.taskadapter.connector.basecamp.classic;

class TestBasecampConfig {
    private static final String PROJECT_KEY = "11125578-task-adapter";

    static BasecampConfig create() {
        BasecampConfig config = new BasecampConfig();
        config.setServerUrl("https://bigtopgames.basecamphq.com");
        config.setApiKey("593cb559d2fe0cc143a1c14390a896bf3be04bb6");
        config.setProjectKey(PROJECT_KEY);
//        config.setLookupUsersByName(true);
        return config;
    }
}
