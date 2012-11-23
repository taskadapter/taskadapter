package com.taskadapter.auth.check;

import com.taskadapter.auth.AuthException;

/**
 * Authentication checker and generator.
 * 
 * @param <K>
 *            key type.
 * @param <A>
 *            authentication data type.
 */
public interface AuthFactory<K, A> {
    /**
     * Verifies, if a key is authentic to a value.
     * 
     * @param key
     *            key to check.
     * @param auth
     *            authentication data.
     * @return <code>true</code> iff key is authentic.
     */
    public boolean isAuthentic(K key, A auth);

    /**
     * Creates a new authentication data for a passed key.
     * 
     * @param key
     *            key to generate an auth for.
     * @return auth information for a key.
     */
    public A generateAuth(K key) throws AuthException;
}
