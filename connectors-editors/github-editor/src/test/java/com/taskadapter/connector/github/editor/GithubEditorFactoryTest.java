package com.taskadapter.connector.github.editor;

import com.taskadapter.connector.github.GithubConfig;
import com.taskadapter.connector.testlib.FileBasedTest;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.service.Sandbox;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class GithubEditorFactoryTest extends FileBasedTest {
    @Test
    public void miniPanelIsCreated() {
        GithubEditorFactory factory = new GithubEditorFactory();
        WindowProvider provider = mock(WindowProvider.class);
        factory.getMiniPanelContents(
                provider,
                new Sandbox(false, tempFolder),
                new GithubConfig());
    }
}
