package com.taskadapter.connector.redmine.editor;

import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.connector.testlib.FileBasedTest;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.service.EditorManager;
import com.taskadapter.web.service.Services;
import org.junit.Test;

import java.util.Collections;

import static org.mockito.Mockito.mock;

public class RedmineEditorFactoryTest extends FileBasedTest {
    @Test
    public void miniPanelIsCreated() {
        RedmineEditorFactory factory = new RedmineEditorFactory();
        WindowProvider provider = mock(WindowProvider.class);
        factory.getMiniPanelContents(provider,
                new Services(tempFolder, new EditorManager(Collections.<String, PluginEditorFactory<?>>emptyMap())),
                new RedmineConfig(), Collections.<RedmineConfig>emptyList());
    }
}
