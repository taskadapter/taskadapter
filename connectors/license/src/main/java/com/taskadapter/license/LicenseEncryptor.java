package com.taskadapter.license;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

final class LicenseEncryptor {

    // TODO this is not very secure, but should be OK for the prototype
    private static final String PASSWORD = "$z823nV_sz#84";

    // secret description:
    // proksorit i vsego delov
    public static String chiper(String text) {
        String result = null;
        byte[] strBuf = text.getBytes();
        byte[] keyBuf = PASSWORD.getBytes();
        int c = 0;
        int z = keyBuf.length;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(strBuf.length);
        for (byte bS : strBuf) {
            byte bK = keyBuf[c];
            byte bO = (byte) (bS ^ bK);
            if (c < z - 1) {
                c++;
            } else {
                c = 0;
            }
            baos.write(bO);
        }
        try {
            baos.flush();
            result = baos.toString();
            baos.close();
            baos = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
