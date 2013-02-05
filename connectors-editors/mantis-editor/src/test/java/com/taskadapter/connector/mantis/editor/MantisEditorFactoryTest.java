package com.taskadapter.connector.mantis.editor;

import com.taskadapter.connector.mantis.MantisConfig;
import com.taskadapter.connector.testlib.FileBasedTest;
import com.taskadapter.web.service.Sandbox;
import org.junit.Test;

public class MantisEditorFactoryTest extends FileBasedTest {
    @Test
    public void miniPanelInstanceIsCreated() {
        MantisEditorFactory factory = new MantisEditorFactory();
        factory.getMiniPanelContents(new Sandbox(false, tempFolder), new MantisConfig());
    }
}
