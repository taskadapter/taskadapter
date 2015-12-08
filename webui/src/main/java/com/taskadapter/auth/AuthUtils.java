package com.taskadapter.auth;

/**
 * Authentication utilities.
 * 
 * @author maxkar
 * 
 */
public final class AuthUtils {

    /**
     * Hexed digits.
     */
    private static final String DIGITS = "0123456789ABCDEF";

    public AuthUtils() {
    }

    public static String bytesToString(byte[] bytes) {
        final StringBuilder resBuilder = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            resBuilder.append(AuthUtils.DIGITS.charAt((b >> 4) & 0x0F));
            resBuilder.append(AuthUtils.DIGITS.charAt(b & 0x0F));
        }
        return resBuilder.toString();
    }

    /**
     * Decodes a string into a bytes. Usable only for results of
     * {@link #bytesToString(byte[])}.
     * 
     * @param item
     *            item to decode.
     * @return byte string.
     */
    public static byte[] stringToBytes(String item) {
        if (item.length() % 2 != 0) {
            return null;
        }
        final int byteCount = item.length() / 2;
        final byte[] res = new byte[byteCount];
        for (int i = 0; i < byteCount; i++) {
            final int highCode = AuthUtils.digitToInt(item.charAt(2 * i));
            final int lowCode = AuthUtils.digitToInt(item.charAt(2 * i + 1));
            if (lowCode < 0 || highCode < 0) {
                return null;
            }
            res[i] = (byte) ((highCode * 16) + lowCode);
        }
        return res;
    }

    private static int digitToInt(char digit) {
        if ('0' <= digit && digit <= '9') {
            return digit - '0';
        }
        if ('A' <= digit && digit <= 'F') {
            return digit - 'A' + 10;
        }
        return -1;
    }

}
