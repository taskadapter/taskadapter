package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.taskadapter.model.Field;
import com.taskadapter.model.GTask;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class JiraToGTaskHelper {
    private static final Logger logger = LoggerFactory.getLogger(JiraToGTask.class);

    public static void processCustomFields(CustomFieldResolver resolver, Issue issue, GTask task) {
        issue.getFields().forEach(f -> {
            if (f.getId().startsWith("customfield")) {
                // custom field
                var maybeField = resolver.getField(f);
                maybeField.ifPresent(
                        field -> processOneField(field, task, f.getValue()));
            }
        });
    }

    private static <T> void processOneField(Field<T> field, GTask task, Object nativeValue) {
        try {
            var value = convertToGenericValue(field, nativeValue);
            task.setValue(field, value);
        } catch (JSONException e) {
            logger.error("Exception while converting JIRA value to generic one: ", e);
        }
    }

    private static <T> T convertToGenericValue(Field<T> field, Object nativeValue) throws JSONException {
        if (nativeValue == null) {
            return null;
        }
        if (nativeValue instanceof JSONObject) {
            return (T) parseJsonObject((JSONObject) nativeValue);
        }
        if (nativeValue instanceof JSONArray) {
            return (T) parseJsonArray(field, (JSONArray) nativeValue);
        }
        return (T) nativeValue.toString();
    }

    private static <T> T parseJsonArray(Field<T> field, JSONArray array) throws JSONException {
        var result = new ArrayList<T>();
        for (int i = 0; i < array.length(); i++) {
            var o = array.get(i);
            result.add(convertToGenericValue(field, o));
        }
        return (T) result;
    }

    private static String parseJsonObject(JSONObject jsonObject) {
        var value = "";
        try {
            value = jsonObject.getString("value");
        } catch (Exception e) {
            value = "";
        }
        return value;
    }

}
