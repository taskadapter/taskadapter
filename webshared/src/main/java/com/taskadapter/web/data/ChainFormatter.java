package com.taskadapter.web.data;

import java.util.Collection;

/**
 * Chain data formatter. Return first non-null result (if any).
 * 
 * @author maxkar
 * 
 * @param <T>
 *            type of formatted data.
 */
final class ChainFormatter<T> implements DataFormatter<T> {
    private final Collection<DataFormatter<T>> peers;

    /**
     * Creates a new chain formatter.
     * 
     * @param peers
     *            peer formatters.
     */
    public ChainFormatter(Collection<DataFormatter<T>> peers) {
        this.peers = peers;
    }

    @Override
    public String format(T data) {
        for (DataFormatter<T> peer : peers) {
            final String guess = peer.format(data);
            if (guess != null) {
                return guess;
            }
        }
        return null;
    }

}
