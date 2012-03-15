package com.taskadapter.connector.common;

import com.google.common.base.Strings;
import org.apache.commons.codec.binary.Base64;

/**
 * Encrypts/decrypts string using long key for XOR method and BASE64 encoding/decoding (RFC-2045).
 * For backward compatibility with old connector configuration files with plain passwords
 * it uses "marker" character which is not contained by BASE64 index table.
 * If string starts with marker it will be processed for decryption.
 * Otherwise it will be returned as is.
 *
 * @author Sergey Safarov
 * @author Igor Laishen
 */
public class XorEncryptor implements Encryptor {
    private static final String DEFAULT_KEY = "@$-TA-KEY-HJB#VJK";
    private static final String DEFAULT_MARKER = "¶";    // any characters but not from BASE64 index table!

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

    /**
     * Encrypts string with key using XOR method and BASE64 encoding
     *
     * @param string string to be encrypted
     * @param key key for XOR method
     * @return Encrypted string
     * @throws Exception
     */
    @Override
    public String encrypt(String string, String key) throws Exception {
        return !Strings.isNullOrEmpty(string)
                    && !Strings.isNullOrEmpty(key)
                ? marker + Base64.encodeBase64String(xorWithKey(string.getBytes(), key.getBytes()))
                : string;
    }

    /**
     * Decrypts string with key using XOR method and BASE64 decoding
     *
     * @param string string to be decrypted
     * @param key key used for XOR method
     * @return Decrypted string
     * @throws Exception
     */
    @Override
    public String decrypt(String string, String key) throws Exception {
        return !Strings.isNullOrEmpty(string)
                    && !Strings.isNullOrEmpty(key)
                    && string.startsWith(marker)
                ? new String(xorWithKey(Base64.decodeBase64(string.substring(marker.length())), key.getBytes()))
                : string;
    }

    /**
     * Encrypts string with default key
     *
     * @param string string to be encrypted
     * @return Encrypted string
     * @throws Exception
     */
    @Override
    public String encrypt(String string) throws Exception {
        return encrypt(string, DEFAULT_KEY);
    }

    /**
     * Decrypts string with default key
     *
     * @param string string to be decrypted
     * @return Decrypted string
     * @throws Exception
     */
    @Override
    public String decrypt(String string) throws Exception {
        return decrypt(string, DEFAULT_KEY);
    }

    /**
     * Encrypts/decrypts with XOR method
     *
     * @param in input byte array for encryption
     * @param key key for XOR encryption
     * @return Encrypted byte array
     */
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
