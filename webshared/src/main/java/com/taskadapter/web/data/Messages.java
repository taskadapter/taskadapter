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

    /**
     * @return the value or the key string with some decoration around it if there's no value for the given key.
     */
    public String get(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    /**
     * @return NULL if the key does not have any corresponding value.
     */
    public String getNoDefault(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return null;
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
