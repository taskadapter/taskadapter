package com.taskadapter.connector.basecamp;

import java.text.ParseException;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;

import com.taskadapter.connector.definition.exceptions.CommunicationException;

/**
 * Json utilities.
 * 
 */
public final class JsonUtils {
    private static final ThreadLocalDateFormat SHORT_DATE = new ThreadLocalDateFormat(
            "yyyy-MM-dd");
    private static final ThreadLocalDateFormat LONG_DATE = new ThreadLocalDateFormat(
            "yyyy-MM-dd HH:mm:ssZ");

    public static JSONArray getOptArray(String field, JSONObject obj)
            throws CommunicationException {
        if (!obj.has(field) || obj.isNull(field)) {
            return null;
        }
        try {
            return obj.getJSONArray(field);
        } catch (JSONException e) {
            throw new CommunicationException("Illegal field value " + field
                    + " in " + obj.toString());
        }
    }

    public static JSONObject getOptObject(String field, JSONObject obj)
            throws CommunicationException {
        if (!obj.has(field) || obj.isNull(field)) {
            return null;
        }
        try {
            return obj.getJSONObject(field);
        } catch (JSONException e) {
            throw new CommunicationException("Illegal field value " + field
                    + " in " + obj.toString());
        }
    }

    public static Date getOptShortDate(String field, JSONObject obj)
            throws CommunicationException {
        final String str = getOptString(field, obj);
        if (str == null) {
            return null;
        }
        try {
            return SHORT_DATE.get().parse(str);
        } catch (ParseException e) {
            throw new CommunicationException(e);
        }
    }

    public static Date getOptLongDate(String field, JSONObject obj)
            throws CommunicationException {
        String str = getOptString(field, obj);
        if (str == null) {
            return null;
        }

        if (str.length() == 29
                && str.charAt(19) == '.'
                && Character.isDigit(str.charAt(20))
                && Character.isDigit(str.charAt(21))
                        && Character.isDigit(str.charAt(22))) {
            str = str.substring(0, 19) + str.substring(23); 
        }

        if (str.length() != 25 || str.charAt(10) != 'T'
                || str.charAt(22) != ':') {
            throw new CommunicationException("Unrecognized date " + str);
        }
        final String str1 = str.substring(0, 10) + " " + str.substring(11, 22)
                + str.substring(23);
        try {
            return LONG_DATE.get().parse(str1);
        } catch (ParseException e) {
            throw new CommunicationException(e);
        }
    }

    public static boolean getOptBool(String field, JSONObject obj)
            throws CommunicationException {
        if (!obj.has(field) || obj.isNull(field)) {
            return false;
        }
        try {
            return obj.getBoolean(field);
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

    public static int getInt(String field, JSONObject obj)
            throws CommunicationException {
        if (!obj.has(field) || obj.isNull(field)) {
            throw new CommunicationException("Required field " + field
                    + " is not set in " + obj.toString());
        }
        try {
            return obj.getInt(field);
        } catch (JSONException e) {
            throw new CommunicationException("Illegal field value " + field
                    + " in " + obj.toString());
        }
    }

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

    public static int genLen(JSONArray arr) {
        return arr == null ? 0 : arr.length();
    }

    public static void writeOpt(JSONWriter writer, String field, String value)
            throws JSONException {
        if (field == null || value == null) {
            return;
        }
        writer.key(field).value(value);
    }

    public static void writeOpt(JSONWriter writer, String field, Boolean value)
            throws JSONException {
        if (field == null || value == null) {
            return;
        }
        writer.key(field).value(value.booleanValue());
    }

    public static void writeShort(JSONWriter writer, String field, Date value)
            throws JSONException {
        if (field == null) {
            return;
        }
        writer.key(field);
        if (value == null)
            writer.value(null);
        else
            writer.value(SHORT_DATE.get().format(value));
    }
}
