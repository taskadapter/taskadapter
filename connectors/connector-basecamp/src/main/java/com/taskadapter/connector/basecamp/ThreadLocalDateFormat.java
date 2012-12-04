package com.taskadapter.connector.basecamp;

import java.text.SimpleDateFormat;

public class ThreadLocalDateFormat extends ThreadLocal<SimpleDateFormat> {
    private final String formatString;

    public ThreadLocalDateFormat(String formatString) {
        this.formatString = formatString;
    }

    @Override
    protected SimpleDateFormat initialValue() {
        return new SimpleDateFormat(formatString);
    }
}
