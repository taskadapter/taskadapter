package com.taskadapter.connector.common;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: sergeys
 * Date: 10.03.12
 * Time: 11:34
 */

public class XorUtils {
    public static String XORKey = "@$-TA-KEY-HJB#VJK";
    public static String XORMark = "¶";

    public static class stringXOR {

            public static String encode(String s, String key) {
                return base64Encode(xorWithKey(s.getBytes(), key.getBytes()));
            }

            public static String decode(String s, String key) {
                return new String(xorWithKey(base64Decode(s), key.getBytes()));
            }

            private static byte[] xorWithKey(byte[] a, byte[] key) {
                byte[] out = new byte[a.length];
                for (int i = 0; i < a.length; i++) {
                    out[i] = (byte) (a[i] ^ key[i%key.length]);
                }
                return out;
            }

            private static byte[] base64Decode(String s) {
                try {
                    BASE64Decoder d = new BASE64Decoder();
                    return d.decodeBuffer(s);
                } catch (IOException e) {throw new RuntimeException(e);}
            }

            private static String base64Encode(byte[] bytes) {
                BASE64Encoder enc = new BASE64Encoder();
                return enc.encode(bytes).replaceAll("\\s", "");

            }

    }

}
