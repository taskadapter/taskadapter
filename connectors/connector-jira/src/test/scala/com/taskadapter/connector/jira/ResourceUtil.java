package com.taskadapter.connector.jira;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

public class ResourceUtil {
    public static JSONObject getJsonObjectFromResource(String resourcePath) {
        try {
            String s = Resources.toString(Resources.getResource(resourcePath), Charsets.UTF_8);
            return new JSONObject(s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static JSONArray getJsonArrayFromResource(String resourcePath) {
        try {
            String s = Resources.toString(Resources.getResource(resourcePath), Charsets.UTF_8);
            return new JSONArray(s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
