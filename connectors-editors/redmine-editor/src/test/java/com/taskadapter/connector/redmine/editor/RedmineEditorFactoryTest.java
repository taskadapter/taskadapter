package com.taskadapter.connector.redmine.editor;

import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.editor.testlib.VaadinTestHelper;
import com.taskadapter.web.service.Sandbox;
import com.vaadin.server.ServiceException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class RedmineEditorFactoryTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void before() throws ServiceException {
        VaadinTestHelper.initVaadinSession(getClass());
    }

    @Test
    public void miniPanelIsCreated() {
        RedmineEditorFactory factory = new RedmineEditorFactory();
        factory.getMiniPanelContents(new Sandbox(true, tempFolder.getRoot()),
                new RedmineConfig(), new WebServerInfo());
    }
}
