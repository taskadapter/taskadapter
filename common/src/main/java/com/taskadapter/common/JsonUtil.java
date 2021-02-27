package com.taskadapter.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.taskadapter.connector.common.ConfigUtils;

public class JsonUtil {
    public static String toJsonString(Object o) {
        return ConfigUtils.createDefaultGson().toJsonTree(o).toString();
    }

    public static JsonElement toJsonElement(String s) {
        return new JsonParser().parse(s);
    }

    public static <T> T parseJson(JsonElement jsonElement, Class<T> clazz) {
        return ConfigUtils.createDefaultGson().fromJson(jsonElement, clazz);
    }

    public static <T> T parseJsonString(String jsonString, Class<T> clazz) {
        return parseJson(toJsonElement(jsonString), clazz);
    }
}
