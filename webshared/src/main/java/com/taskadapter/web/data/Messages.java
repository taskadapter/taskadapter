package com.taskadapter.web.data;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
    private final ResourceBundle bundle;

    public Messages(String bundle) {
        this.bundle = ResourceBundle.getBundle(bundle);
    }

    public Messages(String bundle, Locale locale) {
        this.bundle = ResourceBundle.getBundle(bundle, locale);
    }

    public String get(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    /**
     * Formats a message.
     * 
     * @param key
     *            key.
     * @param args
     *            args.
     * @return formatted message.
     */
    public String format(String key, Object... args) {
        return String.format(get(key), args);
    }
}
