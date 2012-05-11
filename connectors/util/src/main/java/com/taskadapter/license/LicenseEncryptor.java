package com.taskadapter.license;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

final class LicenseEncryptor {

    private String password;

    LicenseEncryptor(String password) {
        this.password = password;
    }

    // secret description:
    // proksorit i vsego delov
    public String chiper(String text) {
        String result = null;
        byte[] strBuf = text.getBytes();
        byte[] keyBuf = password.getBytes();
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
