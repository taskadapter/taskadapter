package com.taskadapter.auth.check;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taskadapter.auth.AuthException;
import com.taskadapter.auth.AuthUtils;

/**
 * HMAC-based authenticator with pluggable algoritm name.
 * 
 */
public class HMacAuthenticator implements AuthFactory<String, String> {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(HMacAuthenticator.class);

    /**
     * Separator for components in a path.
     */
    private static final char COMPONENT_SEPARATOR = ':';

    /**
     * Key charset.
     */
    private static final String KEY_CHARSET = "UTF-8";

    /**
     * Minimal number of salt characters.
     */
    private static final int MIN_SALT_CHARS = 8;

    /**
     * Default salt bytes strength.
     */
    private static final int DEFAULT_SALT_BYTES = 160 / 8;

    /**
     * Algorithm to use.
     */
    private final String algorithm;

    public HMacAuthenticator(String algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public boolean isAuthentic(String key, String auth) {
        final int sepIdx = auth.indexOf(COMPONENT_SEPARATOR);
        if (sepIdx <= MIN_SALT_CHARS) {
            return false;
        }
        final String salt = auth.substring(0, sepIdx);
        auth = auth.substring(sepIdx + 1);

        final byte[] saltBytes = AuthUtils.stringToBytes(salt);
        final byte[] authBytes = AuthUtils.stringToBytes(auth);
        final byte[] keyBytes;
        try {
            keyBytes = key.getBytes(KEY_CHARSET);
        } catch (UnsupportedEncodingException e) {
            return false;
        }
        if (saltBytes == null || authBytes == null) {
            return false;
        }

        return isBytesAuthentic(keyBytes, saltBytes, authBytes);
    }

    private boolean isBytesAuthentic(byte[] keyBytes, byte[] saltBytes,
            byte[] authBytes) {
        final SecretKey key = new SecretKeySpec(saltBytes, algorithm);
        final Mac mac;
        try {
            mac = Mac.getInstance(algorithm);
            mac.init(key);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.info("Error", e);
            return false;
        } catch (InvalidKeyException e) {
            LOGGER.info("Error", e);
            return false;
        }
        mac.update(keyBytes);
        return Arrays.equals(authBytes, mac.doFinal());
    }

    @Override
    public String generateAuth(String key) throws AuthException {
        final byte[] keyBytes;
        try {
            keyBytes = key.getBytes(KEY_CHARSET);
        } catch (UnsupportedEncodingException e) {
            LOGGER.info("Bad bytes", e);
            throw new AuthException("Bad platform, encoding is not supported");
        }
        final SecureRandom random = new SecureRandom();
        final byte[] saltBytes = new byte[DEFAULT_SALT_BYTES];
        random.nextBytes(saltBytes);
        final byte[] authBytes = generateKey(keyBytes, saltBytes);
        return AuthUtils.bytesToString(saltBytes) + COMPONENT_SEPARATOR
                + AuthUtils.bytesToString(authBytes);
    }

    private byte[] generateKey(byte[] keyBytes, byte[] saltBytes)
            throws AuthException {
        final SecretKey key = new SecretKeySpec(saltBytes, algorithm);
        final Mac mac;
        try {
            mac = Mac.getInstance(algorithm);
            mac.init(key);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.info("Error", e);
            throw new AuthException("Bad configuration");
        } catch (InvalidKeyException e) {
            LOGGER.info("Error", e);
            throw new AuthException("Internal error");
        }
        mac.update(keyBytes);
        return mac.doFinal();
    }

}
