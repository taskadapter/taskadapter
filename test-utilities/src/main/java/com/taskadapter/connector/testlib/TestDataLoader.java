package com.taskadapter.connector.testlib;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;

public class TestDataLoader {
    public static Object load(String fileName, Type fooType) {
        try {
            String fileContents = Resources.toString(Resources.getResource(fileName), Charsets.UTF_8);
            return new Gson().fromJson(fileContents, fooType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
