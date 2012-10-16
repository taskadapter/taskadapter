package com.taskadapter.connector.github.editor;

import com.taskadapter.connector.github.GithubConfig;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.service.EditorManager;
import com.taskadapter.web.service.Services;
import org.junit.Test;

import java.io.File;
import java.util.Collections;

import static org.mockito.Mockito.mock;

public class GithubEditorFactoryTest {
    @Test
    public void miniPanelIsCreated() {
        GithubEditorFactory factory = new GithubEditorFactory();
        WindowProvider provider = mock(WindowProvider.class);
        factory.getMiniPanelContents(
                provider,
                new Services(new File("tmp"), new EditorManager(Collections.<String,PluginEditorFactory<?>>emptyMap())),
                new GithubConfig());
    }
}
