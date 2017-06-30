package com.taskadapter.connector;

import com.google.common.base.Charsets;
import com.google.common.io.CharSource;
import com.google.common.io.Resources;

import java.io.Reader;
import java.net.URL;
import java.util.Properties;

public class PropertiesUtf8Loader {
    public static Properties load(String resourceFileName) {
        Properties properties = new Properties();
        URL resource = Resources.getResource(resourceFileName);
        try {
            final CharSource source = Resources.asCharSource(resource, Charsets.UTF_8);
            try (Reader inputStream = source.openStream()) {
                properties.load(inputStream);
                properties.list(System.out);
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot load properties file " + resourceFileName);
        }
        return properties;
    }
}
