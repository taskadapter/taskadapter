package com.taskadapter.web.data;

import java.util.Map;

/**
 * Class-map formatter. Formats each class with a specific formatter. Returns
 * <code>null</code>, if data is <code>null</code> or no appropriate formatter
 * found.
 * 
 * @author maxkar
 * 
 */
final class ClassmapFormatter<T> implements DataFormatter<T> {

    /**
     * Class details formatter.
     */
    private final Map<Class<? extends T>, DataFormatter<T>> formatters;

    public ClassmapFormatter(
            Map<Class<? extends T>, DataFormatter<T>> formatters) {
        this.formatters = formatters;
    }

    @Override
    public String format(T data) {
        if (data == null) {
            return null;
        }
        
        final Class<?> actual = data.getClass();
        final DataFormatter<T> guess = formatters.get(actual);
        
        if (guess == null) {
            return null;
        }
        
        return guess.format(data);
    }

}
