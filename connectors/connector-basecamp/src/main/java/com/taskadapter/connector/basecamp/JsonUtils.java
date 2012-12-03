package com.taskadapter.connector.basecamp;

import org.json.JSONException;
import org.json.JSONObject;

import com.taskadapter.connector.definition.exceptions.CommunicationException;

/**
 * Json utilities.
 * 
 */
public final class JsonUtils {
    public static long getLong(String field, JSONObject obj)
            throws CommunicationException {
        if (!obj.has(field) || obj.isNull(field)) {
            throw new CommunicationException("Required field " + field
                    + " is not set in " + obj.toString());
        }
        try {
            return obj.getLong(field);
        } catch (JSONException e) {
            throw new CommunicationException("Illegal field value " + field
                    + " in " + obj.toString());
        }
    }

    public static String getString(String field, JSONObject obj)
            throws CommunicationException {
        if (!obj.has(field) || obj.isNull(field)) {
            throw new CommunicationException("Required field " + field
                    + " is not set in " + obj.toString());
        }
        try {
            return obj.getString(field);
        } catch (JSONException e) {
            throw new CommunicationException("Illegal field value " + field
                    + " in " + obj.toString());
        }
    }

    public static String getOptString(String field, JSONObject obj)
            throws CommunicationException {
        if (!obj.has(field) || obj.isNull(field)) {
            return null;
        }
        try {
            return obj.getString(field);
        } catch (JSONException e) {
            throw new CommunicationException("Illegal field value " + field
                    + " in " + obj.toString());
        }
    }
}
