package com.taskadapter.connector.github.editor;

import com.taskadapter.connector.github.GithubConfig;
import com.taskadapter.connector.testlib.FileBasedTest;
import com.taskadapter.web.service.Sandbox;
import org.junit.Test;

public class GithubEditorFactoryTest extends FileBasedTest {
    @Test
    public void miniPanelIsCreated() {
        GithubEditorFactory factory = new GithubEditorFactory();
        factory.getMiniPanelContents(
                new Sandbox(false, tempFolder),
                new GithubConfig());
    }
}
