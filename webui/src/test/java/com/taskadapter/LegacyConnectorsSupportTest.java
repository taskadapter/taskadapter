package com.taskadapter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LegacyConnectorsSupportTest {
    @Test
    public void legacyMSPIsResolved() throws Exception {
        assertEquals("Microsoft Project", LegacyConnectorsSupport.getRealId("Microsoft Project (XML)"));
    }

    @Test
    public void otherIDIsNotChanged() throws Exception {
        assertEquals("something", LegacyConnectorsSupport.getRealId("something"));
    }

}
