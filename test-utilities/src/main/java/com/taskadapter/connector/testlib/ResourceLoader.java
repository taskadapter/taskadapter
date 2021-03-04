package com.taskadapter.connector.testlib;

public class ResourceLoader {
    public static String getAbsolutePathForResource(String name) {
        var url = ResourceLoader.class.getClassLoader().getResource(name);
        try {
            return url.toURI().getPath();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
