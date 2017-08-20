package com.taskadapter.connector.github.editor;

import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.github.GithubConfig;
import com.taskadapter.web.service.Sandbox;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import scala.Option;

public class GithubEditorFactoryTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void miniPanelIsCreated() {
        GithubEditorFactory factory = new GithubEditorFactory();
        factory.getMiniPanelContents(
                new Sandbox(false, tempFolder.getRoot()),
                new GithubConfig(),
                new WebConnectorSetup("", Option.empty(), "", "", "", "", true, ""));
    }
}
