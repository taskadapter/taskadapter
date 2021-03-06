package com.taskadapter.connector.common;

import java.util.List;

public class ValueTypeResolver {
    public static Float getValueAsFloat(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return Float.parseFloat((String) value);
        }
        if (value instanceof Integer) {
            return ((Integer) value).floatValue();
        }
        return (Float) value;
    }

    public static Integer getValueAsInt(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return Integer.parseInt((String) value);
        }
        if (value instanceof Float) {
            return ((Float) value).intValue();
        }
        return (Integer) value;
    }

    public static String getValueAsString(Object value) {
        if (value == null) {
            return "";
        }

        if (value instanceof List) {
            List seq = (List) value;
            if (seq.isEmpty()) {
                return "";
            }
            return seq.get(0).toString();
        }

        if (value instanceof String) {
            return (String) value;
        }

        throw new RuntimeException("Cannot parse value " + value);
    }
}
