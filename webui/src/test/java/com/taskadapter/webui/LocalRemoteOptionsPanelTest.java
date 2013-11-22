package com.taskadapter.webui;

import com.taskadapter.connector.testlib.FileBasedTest;
import com.taskadapter.webui.service.Services;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LocalRemoteOptionsPanelTest extends FileBasedTest {

    private Services services;

    @Before
    public void beforeEachTest() {
        super.beforeEachTest();
        services = TestServicesFactory.createServices(tempFolder);
    }

    @Test
    public void radioButtonIsEnabledForAdmin() {
        LocalRemoteOptionsPanel panel = new LocalRemoteOptionsPanel(services);
        // TODO there's no API to login as a certain user in tests.
        // so this test fails.
        assertTrue(panel.getGroup().isEnabled());
    }

    @Test
    public void radioButtonIsDisabledForNonAdmin() {
        LocalRemoteOptionsPanel panel = new LocalRemoteOptionsPanel(services);
        assertFalse(panel.getGroup().isEnabled());
    }
}
