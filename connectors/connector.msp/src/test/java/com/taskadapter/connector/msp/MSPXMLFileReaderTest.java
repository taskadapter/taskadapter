package com.taskadapter.connector.msp;

import org.junit.Assert;
import org.junit.Test;


public class MSPXMLFileReaderTest {

    @Test
    public void readFile() throws Exception {
         Assert.assertNotNull(MSPTestUtils.readTestProjectFile());
    }
}
