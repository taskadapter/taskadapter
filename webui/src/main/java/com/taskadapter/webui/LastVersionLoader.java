package com.taskadapter.webui;

import com.taskadapter.connector.PropertiesUtf8Loader;
import com.taskadapter.http.HttpCaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LastVersionLoader {
    private static final Logger log = LoggerFactory.getLogger("default");

    /**
     * check the last TA version available for download on the website.
     */
    public static String loadLastVersion() {
        var properties = PropertiesUtf8Loader.load("taskadapter.properties");
        var url = properties.getProperty("update_site_url");
        try {
            var lastVersionString = HttpCaller.getAsString(url);
            return lastVersionString.trim();
        } catch (Exception e) {
            log.error("Cannot load last version info: " + e.toString());
            return "unknown";
        }
    }
}
