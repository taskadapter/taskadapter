package com.taskadapter.connector.trello;

import com.taskadapter.connector.PropertiesUtf8Loader;
import com.taskadapter.connector.definition.WebConnectorSetup;

import java.util.Properties;

public class TrelloTestConfig {

    private static final String TEST_PROPERTIES = "trello.properties";
    private static final Properties properties = PropertiesUtf8Loader.load(TEST_PROPERTIES);

    static TrelloConfig getConfig() {
        var config = new TrelloConfig();
        config.setBoardId(properties.getProperty("boardId"));
        config.setBoardName("");
        return config;
    }

    static WebConnectorSetup getSetup() {
        return WebConnectorSetup.apply(TrelloConnector.ID, "label1",
                "", "", properties.getProperty("apikey"),
                true, properties.getProperty("token"));
    }
}
