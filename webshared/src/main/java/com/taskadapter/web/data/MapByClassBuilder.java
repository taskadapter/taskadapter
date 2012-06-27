package com.taskadapter.web.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.taskadapter.web.magic.Interfaces;

/**
 * Canonical and simple way to create a
 * {@link DataFormatters#mapByClass(java.util.Map)} formatter.
 * 
 * @author maxkar
 * 
 */
public final class MapByClassBuilder<T> {
    /**
     * Used formatters.
     */
    private final Map<Class<? extends T>, DataFormatter<T>> formatters = new HashMap<Class<? extends T>, DataFormatter<T>>();

    /**
     * Default formatter.
     */
    private DataFormatter<T> defaultFormatter;

    /**
     * Adds a new formatter.
     * 
     * @param clazz
     *            class to format.
     * @param formatter
     *            used "Details" formatter.
     * @return this to allow call chaining.
     * @throws IllegalArgumentException
     *             if mapping for clazz is already defined.
     */
    @SuppressWarnings("unchecked")
    public MapByClassBuilder<T> add(Class<? extends T> clazz,
            DataFormatter<? extends T> formatter) {
        if (formatters.containsKey(formatter)) {
            throw new IllegalArgumentException("Mapping for class " + clazz
                    + " is already present in this builder");
        }
        formatters.put(clazz, (DataFormatter<T>) formatter);
        return this;
    }

    /**
     * Adds a new formatter.
     * 
     * @param clazz
     *            class to format.
     * @param message
     *            fixed "format" message, used for all instances of this class.
     * @return this to allow call chaining.
     * @throws IllegalArgumentException
     *             if mapping for clazz is already defined.
     */
    public MapByClassBuilder<T> add(Class<? extends T> clazz, String message) {
        return add(clazz, DataFormatters.<T> fixed(message));
    }

    /**
     * Adds a new formatter from a specified method.
     * 
     * @param clazz
     *            class to format.
     * @param object
     *            object or class (for static methods) to invoke a method on.
     * @param method
     *            method name to invoke.
     * @param extra
     *            arguments. Value to convert is added last.
     * @return this to allow call chaining.
     * @throws IllegalArgumentException
     *             if mapping for clazz is already defined.
     * @throws IllegalArgumentException
     *             if method does not denotes an unique method on a specified
     *             object.
     */
    @SuppressWarnings("unchecked")
    public MapByClassBuilder<T> addMethod(Class<? extends T> clazz,
            Object object, String method, Object... args) {
        return add(clazz, (DataFormatter<T>) Interfaces.fromMethod(
                DataFormatter.class, object, method, args));
    }

    /**
     * Requires to use a default formatter.
     * 
     * @param formatter
     *            default formatter.
     * @return this to allow call chaining.
     * @throws IllegalArgumentException
     *             if default formatter is already defined.
     */
    public MapByClassBuilder<T> useDefault(DataFormatter<T> formatter) {
        if (this.defaultFormatter != null)
            throw new IllegalArgumentException(
                    "Default formatter is already defined");
        this.defaultFormatter = formatter;
        return this;
    }

    /**
     * Requires to use a message as a default formatter.
     * 
     * @param msg
     *            default message.
     * @return this to allow call chaining.
     * @throws IllegalArgumentException
     *             if default formatter is already defined.
     */
    public MapByClassBuilder<T> useDefault(String msg) {
        return useDefault(DataFormatters.<T> fixed(msg));
    }

    /**
     * Requires to use a specified method as a default formatter.
     * 
     * @param object
     *            object or class (for static methods) to invoke a method on.
     * @param method
     *            method name to invoke.
     * @param extra
     *            arguments. Value to convert is added last.
     * @return this to allow call chaining.
     * @throws IllegalArgumentException
     *             if method does not denotes an unique method on a specified
     *             object.
     * @throws IllegalArgumentException
     *             if default formatter is already defined.
     */
    @SuppressWarnings("unchecked")
    public MapByClassBuilder<T> useDefaultMethod(Object object, String method,
            Object... args) {
        return useDefault((DataFormatter<T>) Interfaces.fromMethod(
                DataFormatter.class, object, method, args));
    }

    /**
     * Returns a data formatter. New formatter have no relation with this
     * builder and futher modifications of this builder does not affect returned
     * formatter.
     * 
     * @return data formatter.
     */
    public DataFormatter<T> get() {
        final DataFormatter<T> baseFormatter = DataFormatters
                .mapByClass(new HashMap<Class<? extends T>, DataFormatter<T>>(
                        formatters));
        if (defaultFormatter == null) {
            return baseFormatter;
        }

        final List<DataFormatter<T>> peers = new ArrayList<DataFormatter<T>>(2);
        peers.add(baseFormatter);
        peers.add(defaultFormatter);
        return DataFormatters.firstApplicable(peers);
    }

    /**
     * Static access point to start a building.
     * 
     * @return new builder.
     */
    public static <T> MapByClassBuilder<T> build() {
        return new MapByClassBuilder<T>();
    }
}
