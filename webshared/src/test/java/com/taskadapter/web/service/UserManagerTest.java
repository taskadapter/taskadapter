package com.taskadapter.web.service;

import com.taskadapter.connector.testlib.FileBasedTest;
import com.taskadapter.web.PluginEditorFactory;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserManagerTest extends FileBasedTest {
    @Test
    public void firstAdminUserIsCreated() throws Exception {
        Services services = new Services(tempFolder, new EditorManager(Collections.<String, PluginEditorFactory<?>>emptyMap()));

        UserManager userManager = services.getUserManager();
        assertTrue(userManager.getUsers().isEmpty());

        assertEquals(1, userManager.getUsers().size());
        assertTrue("admin", userManager.doesUserExists("admin"));
    }
}
