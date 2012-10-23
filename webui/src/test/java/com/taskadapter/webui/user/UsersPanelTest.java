package com.taskadapter.webui.user;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.taskadapter.connector.testlib.FileBasedTest;
import com.taskadapter.license.LicenseException;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.service.EditorManager;
import com.taskadapter.web.service.Services;
import com.vaadin.ui.Button;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class UsersPanelTest extends FileBasedTest {
    private Services services;

    @Before
    public void beforeEachTest() {
        super.beforeEachTest();
        services = new Services(tempFolder, new EditorManager(Collections.<String, PluginEditorFactory<?>>emptyMap()));
    }

    @Test
    public void addUserButtonIsHiddenWithoutLicense() {
        UsersPanel panel = new UsersPanel(services);
        assertNull(panel.getAddUserButton());
        assertEquals("Can't add users until a license is installed.", panel.getStatusLabelText());
    }

    @Test
    public void addUserButtonIsShownWith5UserLicenseAndNoUsers() throws IOException, LicenseException {
        String validMultiUserLicense = Resources.toString(Resources.getResource("license/taskadapterweb.5-users.license"), Charsets.UTF_8);
        services.getLicenseManager().setNewLicense(validMultiUserLicense);
        UsersPanel panel = new UsersPanel(services);
        assertTrue(panel.getAddUserButton() instanceof Button);
        assertEquals("", panel.getStatusLabelText());
    }

    @Test
    public void addUserButtonIsShownWith5UserLicenseAnd4ExistingUsers() throws IOException, LicenseException {
        String validMultiUserLicense = Resources.toString(Resources.getResource("license/taskadapterweb.5-users.license"), Charsets.UTF_8);
        services.getLicenseManager().setNewLicense(validMultiUserLicense);
        createUsers(services, 4);
        UsersPanel panel = new UsersPanel(services);
        assertEquals("", panel.getStatusLabelText());
        assertTrue(panel.getAddUserButton() instanceof Button);
    }

    @Test
    public void addUserButtonIsHiddenWith5UserLicenseAnd5ExistingUsers() throws IOException, LicenseException {
        String validMultiUserLicense = Resources.toString(Resources.getResource("license/taskadapterweb.5-users.license"), Charsets.UTF_8);
        services.getLicenseManager().setNewLicense(validMultiUserLicense);
        createUsers(services, 5);
        UsersPanel panel = new UsersPanel(services);
        assertNull(panel.getAddUserButton());
        assertEquals("Maximum users number allowed by your license is reached.", panel.getStatusLabelText());
    }

    private void createUsers(Services services, int usersNumber) {
        for (int i = 0; i < usersNumber; i++) {
            services.getUserManager().saveUser("user" + i, "password" + i);
        }
    }

}
