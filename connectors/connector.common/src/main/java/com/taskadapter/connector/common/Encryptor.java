package com.taskadapter.connector.common;

/**
 * @author Igor Laishen
 */
public interface Encryptor {
    public String encrypt(String string, String key);
    public String encrypt(String string);

    public String decrypt(String string, String key);
    public String decrypt(String string);
}
