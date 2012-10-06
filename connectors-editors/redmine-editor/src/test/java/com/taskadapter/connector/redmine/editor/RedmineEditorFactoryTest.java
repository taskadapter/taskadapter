package com.taskadapter.connector.redmine.editor;

import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.service.Services;
import org.junit.Test;

import java.io.File;

import static org.mockito.Mockito.mock;

public class RedmineEditorFactoryTest {
    @Test
    public void miniPanelIsCreated() {
        RedmineEditorFactory factory = new RedmineEditorFactory();
        WindowProvider provider = mock(WindowProvider.class);
        factory.getMiniPanelContents(provider, new Services(new File("tmp")), new RedmineConfig());
    }
}
