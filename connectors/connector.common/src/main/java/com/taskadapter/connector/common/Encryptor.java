package com.taskadapter.connector.common;

/**
 * @author Igor Laishen
 */
public interface Encryptor {
    public String encrypt(String string, String key) throws Exception;
    public String encrypt(String string) throws Exception;

    public String decrypt(String string, String key) throws Exception;
    public String decrypt(String string) throws Exception;
}
