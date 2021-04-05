package com.taskadapter.connector.testlib;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;

public class TestDataLoader {
    public static Object load(String fileName, Type fooType) {
        String fileContents = loadAsString(fileName);
        return new Gson().fromJson(fileContents, fooType);
    }

    public static String loadAsString(String fileName) {
        try {
            return Resources.toString(Resources.getResource(fileName), Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
