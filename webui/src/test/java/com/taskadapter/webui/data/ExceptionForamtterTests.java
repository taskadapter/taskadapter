package com.taskadapter.webui.data;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests an exception formatter.
 * 
 * @author maxkar
 * 
 */
public class ExceptionForamtterTests {
    /**
     * Tests a basic exception.
     */
    @Test
    public void testBasicFormat() {
        Assert.assertEquals("Internal error : Test error", ExceptionFormatter
                .getFormatter().format(new Error("Test error")));
    }
}
