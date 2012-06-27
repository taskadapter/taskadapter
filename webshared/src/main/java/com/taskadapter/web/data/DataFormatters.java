package com.taskadapter.web.data;

import java.util.Collection;
import java.util.Map;

/**
 * Data formatter utilities.
 * 
 * @author maxkar
 * 
 */
public final class DataFormatters {

    private DataFormatters() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a converter, which uses a "data map" converter.
     * 
     * @param formatters
     *            "class details" map.
     * @return used formatter.
     */
    public static <T> DataFormatter<T> mapByClass(
            Map<Class<? extends T>, DataFormatter<T>> formatters) {
        return new ClassmapFormatter<T>(formatters);
    }

    /**
     * Creates a fomatter, which returns a first non-null peer result.
     * 
     * @param formatters
     *            formatters to find a first non-null result.
     * @return data formatter.
     */
    public static <T> DataFormatter<T> firstApplicable(
            Collection<DataFormatter<T>> formatters) {
        return new ChainFormatter<T>(formatters);
    }

    /**
     * "Fixed" data formatter, converts a message to a string.
     * 
     * @param message
     *            message.
     * @return data formatter.
     */
    public static <T> DataFormatter<T> fixed(String message) {
        return new ConstFormatter<T>(message);
    }
}
