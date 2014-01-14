package com.taskadapter.webui;

import com.taskadapter.auth.AuthException;
import com.taskadapter.webui.service.EditableCurrentUserInfo;
import com.taskadapter.webui.service.Services;
import com.taskadapter.webui.service.WrongPasswordException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LocalRemoteOptionsPanelTest {

    private Services services;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void beforeEachTest() {
        services = TestServicesFactory.createServices(tempFolder.getRoot());
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
