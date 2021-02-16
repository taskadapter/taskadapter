package com.taskadapter.connector.common;

public interface Encryptor {
    String encrypt(String string, String key);

    String encrypt(String string);

    String decrypt(String string, String key);

    String decrypt(String string);
}
