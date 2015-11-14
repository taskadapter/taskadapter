package com.taskadapter.web.data;

/**
 * Data formatter.
 * 
 * @author maxkar
 * 
 * @param <T>
 *            type of data to convert.
 */
public interface DataFormatter<T> {
    /**
     * Converts a data to a string.
     * 
     * @param data
     *            data to convert.
     * @return converted string.
     */
    String format(T data);
}
