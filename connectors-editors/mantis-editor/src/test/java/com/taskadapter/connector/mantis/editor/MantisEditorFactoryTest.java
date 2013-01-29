package com.taskadapter.connector.mantis.editor;

import com.taskadapter.connector.mantis.MantisConfig;
import com.taskadapter.connector.testlib.FileBasedTest;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.service.BasicSandbox;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class MantisEditorFactoryTest extends FileBasedTest {
    @Test
    public void miniPanelInstanceIsCreated() {
        MantisEditorFactory factory = new MantisEditorFactory();
        WindowProvider provider = mock(WindowProvider.class);
        factory.getMiniPanelContents(provider,
                new BasicSandbox(false, tempFolder),
                new MantisConfig());
    }
}
