package com.taskadapter.connector.common.data;

import com.taskadapter.connector.definition.exceptions.ConnectorException;

/**
 * Converter between source and destination type.
 * 
 * @param <S>
 *            source type.
 * @param <T>
 *            destination type.
 */
public interface ConnectorConverter<S, T> {
    /**
     * Converts an inssue from a source to a destination.
     * 
     * @param source
     *            source object to convert.
     * @return converted object.
     * @throws ConnectorException
     *             if object cannot be converted.
     */
    T convert(S source) throws ConnectorException;
}
