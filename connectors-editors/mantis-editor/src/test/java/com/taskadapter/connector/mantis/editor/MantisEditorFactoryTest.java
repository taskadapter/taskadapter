package com.taskadapter.connector.mantis.editor;

import com.taskadapter.connector.mantis.MantisConfig;
import com.taskadapter.web.service.Sandbox;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class MantisEditorFactoryTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void miniPanelInstanceIsCreated() {
        MantisEditorFactory factory = new MantisEditorFactory();
        factory.getMiniPanelContents(new Sandbox(false, tempFolder.getRoot()), new MantisConfig());
    }
}
