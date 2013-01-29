package com.taskadapter.connector.mantis.editor;

import com.taskadapter.connector.mantis.MantisConfig;
import com.taskadapter.connector.testlib.FileBasedTest;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.service.Sandbox;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class MantisEditorFactoryTest extends FileBasedTest {
    @Test
    public void miniPanelInstanceIsCreated() {
        MantisEditorFactory factory = new MantisEditorFactory();
        WindowProvider provider = mock(WindowProvider.class);
        factory.getMiniPanelContents(provider,
                new Sandbox(false, tempFolder),
                new MantisConfig());
    }
}
