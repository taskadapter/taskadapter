package com.taskadapter.webui;

import com.taskadapter.auth.AuthException;
import com.taskadapter.connector.testlib.FileBasedTest;
import com.taskadapter.webui.service.EditableCurrentUserInfo;
import com.taskadapter.webui.service.Services;
import com.taskadapter.webui.service.WrongPasswordException;
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
    public void radioButtonIsEnabledForAdmin() throws WrongPasswordException, AuthException {
        EditableCurrentUserInfo userInfo = (EditableCurrentUserInfo) services.getCurrentUserInfo();
        userInfo.setUserName("admin");
        LocalRemoteOptionsPanel panel = new LocalRemoteOptionsPanel(services);
        assertTrue(panel.getGroup().isEnabled());
    }

    @Test
    public void radioButtonIsDisabledForNonAdmin() {
        LocalRemoteOptionsPanel panel = new LocalRemoteOptionsPanel(services);
        assertFalse(panel.getGroup().isEnabled());
    }
}
