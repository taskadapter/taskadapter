package com.taskadapter.connector.msp;

public class NumberConverters {
    static float getFloatNullSafe(Object value) {
        return value == null ? 0 : (Float) value;
    }
}
