package com.taskadapter.auth;

import org.junit.Assert;

import org.junit.Test;

public class AuthUtilsTest {
    @Test
    public void testBytes1() {
        final byte[] bytes = new byte[] {1, 10, 16, 32, 33, 35};
        Assert.assertEquals("010A10202123", AuthUtils.bytesToString(bytes));
    }
}
