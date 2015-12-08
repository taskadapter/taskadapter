package com.taskadapter.webui;

import com.taskadapter.web.data.Messages;

public final class Page {
    private static final String BUNDLE_NAME = "com.taskadapter.webui.data.messages";

    public static final Messages MESSAGES = new Messages(BUNDLE_NAME);

    public static String message(String key) {
        return MESSAGES.get(key);
    }

    public static String message(String key, String... argument) {
        return MESSAGES.format(key, argument);
    }
}
