package com.taskadapter.connector.common;

import org.junit.Assert;
import org.junit.Test;

public class XorEncryptorTest {
    @Test
    public void shouldEncryptWithDefaultMarker() throws Exception {
        Assert.assertEquals("¶cRYeYHQb", new XorEncryptor().encrypt("123456"));
    }

    @Test
    public void shouldReturnSameIfEmptyStringOrKey() throws Exception {
        Assert.assertEquals(null, new XorEncryptor().decrypt(null));
        Assert.assertEquals("", new XorEncryptor().decrypt(""));
        Assert.assertEquals("asd", new XorEncryptor().decrypt("asd", ""));
        Assert.assertEquals("zxc", new XorEncryptor().decrypt("zxc", null));
    }

    @Test
    public void shouldReturnSameIfEmptyOrNotEncrypted() throws Exception {
        Assert.assertEquals("Aa 12#@_^~", new XorEncryptor().decrypt("Aa 12#@_^~"));    // default marker is '¶'
        Assert.assertEquals("Xa 12#@_^~", new XorEncryptor(";").decrypt("Xa 12#@_^~")); // marker is ';'
    }

    @Test
    public void shouldDecryptWithDefaultMarker() throws Exception {
        Assert.assertEquals("Aa 12#@_^~", new XorEncryptor().decrypt("¶AUUNZXMOCxoHUw=="));
    }

    @Test
    public void shouldDecryptPreviouslyEncryptedWithDefaultMarker() throws Exception {
        XorEncryptor encryptor = new XorEncryptor();
        Assert.assertEquals("_-&^%$#34=Hgd@1+", encryptor.decrypt(encryptor.encrypt("_-&^%$#34=Hgd@1+")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWithoutMarker() {
        new XorEncryptor("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWithIncorrectMarker() {
        new XorEncryptor("=");
    }

    @Test
    public void shouldEncryptWithCustomMarker() throws Exception {
        String marker = ";";
        Assert.assertEquals(marker + "cRYeYHQb", new XorEncryptor(marker).encrypt("123456"));
    }

    @Test
    public void shouldDecryptWithCustomMarker() throws Exception {
        String marker = ";";
        Assert.assertEquals("Aa 12#@_^~", new XorEncryptor(marker).decrypt(marker + "AUUNZXMOCxoHUw=="));
    }

    @Test
    public void shouldDecryptPreviouslyEncryptedWithCustomMarker() throws Exception {
        XorEncryptor encryptor = new XorEncryptor(";");
        Assert.assertEquals("_-&^%$#34=Hgd@1+", encryptor.decrypt(encryptor.encrypt("_-&^%$#34=Hgd@1+")));
    }

    @Test
    public void shouldDecryptPreviouslyEncryptedWithCustomKey() throws Exception {
        String key = "$#34=H";
        XorEncryptor encryptor = new XorEncryptor();
        Assert.assertEquals("_ &^%$4#77=Hg-d@1+", encryptor.decrypt(encryptor.encrypt("_ &^%$4#77=Hg-d@1+", key), key));
    }
}
