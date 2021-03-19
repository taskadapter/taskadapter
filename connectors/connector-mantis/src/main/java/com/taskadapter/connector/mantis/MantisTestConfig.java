package com.taskadapter.connector.mantis;

import com.taskadapter.connector.PropertiesUtf8Loader;
import com.taskadapter.connector.definition.WebConnectorSetup;

import java.util.Properties;

public class MantisTestConfig {
    private final static String TEST_PROPERTIES = "mantis.properties";
    private final static Properties properties = PropertiesUtf8Loader.load(TEST_PROPERTIES);

    public static WebConnectorSetup getSetup() {
        return WebConnectorSetup.apply(
                MantisConnector.ID,
                "label1",
                properties.getProperty("uri"),
                properties.getProperty("user"),
                properties.getProperty("password"),
                false, "");
    }
}
