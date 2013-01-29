package com.taskadapter.connector.redmine.editor;

import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.connector.testlib.FileBasedTest;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.service.Sandbox;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class RedmineEditorFactoryTest extends FileBasedTest {
    @Test
    public void miniPanelIsCreated() {
        RedmineEditorFactory factory = new RedmineEditorFactory();
        WindowProvider provider = mock(WindowProvider.class);
        factory.getMiniPanelContents(provider,
                new Sandbox(true, tempFolder),
                new RedmineConfig());
    }
}
