package com.taskadapter.connector.common;

import com.google.common.base.Strings;
import org.apache.commons.codec.binary.Base64;

/**
 * @author Igor Laishen
 */
public class XorEncryptor implements Encryptor {
    private static final String DEFAULT_KEY = "@$-TA-KEY-HJB#VJK";
    private static final String DEFAULT_MARKER = "¶";    // any characters but not from Base64 index table!

    private static final String BASE64_INDEX_TABLE =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/="; //RFC-2045

    private String marker;

    public XorEncryptor() {
        marker = DEFAULT_MARKER;
    }

    public XorEncryptor(String marker) {
        if(Strings.isNullOrEmpty(marker) || BASE64_INDEX_TABLE.contains(marker)) {
            throw new IllegalArgumentException("Correct marker is required");
        }

        this.marker = marker;
    }

    @Override
    public String encrypt(String string, String key) throws Exception {
        return !Strings.isNullOrEmpty(string)
                    && !Strings.isNullOrEmpty(key)
                ? marker + Base64.encodeBase64String(xorWithKey(string.getBytes(), key.getBytes()))
                : string;
    }

    @Override
    public String decrypt(String string, String key) throws Exception {
        return !Strings.isNullOrEmpty(string)
                    && !Strings.isNullOrEmpty(key)
                    && string.startsWith(marker)
                ? new String(xorWithKey(Base64.decodeBase64(string.substring(marker.length())), key.getBytes()))
                : string;
    }

    @Override
    public String encrypt(String string) throws Exception {
        return encrypt(string, DEFAULT_KEY);
    }

    @Override
    public String decrypt(String string) throws Exception {
        return decrypt(string, DEFAULT_KEY);
    }

    private byte[] xorWithKey(byte[] in, byte[] key) {
        int inLength = in.length;
        int keyLength = key.length;

        byte[] out = new byte[inLength];

        for (int i = 0; i < inLength; i++) {
            out[i] = (byte) (in[i] ^ key[i % keyLength]);
        }

        return out;
    }
}
