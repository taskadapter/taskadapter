package com.taskadapter.connector.mantis;

import com.taskadapter.connector.definition.WebServerInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class Config {
    private static final String TEST_PROPERTIES = "mantis.properties";

    private static final Properties properties = new Properties();

    static {
        InputStream is = Config.class.getClassLoader().getResourceAsStream(
                TEST_PROPERTIES);
        if (is == null) {
            throw new RuntimeException("Can't find file " + TEST_PROPERTIES + " in classpath. Please create it using one of the templates");
        }
        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getURI() {
        return properties.getProperty("uri");
    }

    public static String getUserLogin() {
        return properties.getProperty("user");
    }

    public static String getPassword() {
        return properties.getProperty("password");
    }

    public static MantisConfig getMantisTestConfig() {
        WebServerInfo mntInfo = new WebServerInfo(Config.getURI(), Config.getUserLogin(), Config.getPassword());
        MantisConfig mantisConfig = new MantisConfig();
        mantisConfig.setServerInfo(mntInfo);
        return mantisConfig;
    }

}
