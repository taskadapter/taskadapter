package com.taskadapter.connector.msp;

import org.junit.Assert;
import org.junit.Test;

public class MSPConnectorTest {

    @Test
    public void testIDIsUnchanged() {
        // this looks weird, but UI code needs to identify MSP connector.
        // see SyncEditor class: hasMSPConnector()
        // another option would be to add "isMSP" to the Connector Interface, which would be
        // even more weird.
        Assert.assertEquals("Microsoft Project", MSPConnector.ID);
    }
}
