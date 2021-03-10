package com.taskadapter.connector.msp;

import java.util.List;

public class CustomFieldConverter {

    /**
     * @return empty string if given value is null, otherwise converted value. never returns null
     */
    public static String getValueAsString(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof List<?>) {
            List<?> list = (List<?>) value;
            if (list.isEmpty()) {
                return "";
            }
            var first = list.get(0);
            if (first instanceof String) {
                return String.join(" ", (List<String>) list);
            }
            return "unknown type, only list of strings are supported";
        }
        return value.toString();
    }
}
