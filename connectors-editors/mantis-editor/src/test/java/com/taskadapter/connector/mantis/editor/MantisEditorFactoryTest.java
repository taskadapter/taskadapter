package com.taskadapter.connector.mantis.editor;

import com.taskadapter.connector.mantis.MantisConfig;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.service.EditorManager;
import com.taskadapter.web.service.Services;
import org.junit.Test;

import java.io.File;
import java.util.Collections;

import static org.mockito.Mockito.mock;

public class MantisEditorFactoryTest {
    @Test
    public void miniPanelInstanceIsCreated() {
        MantisEditorFactory factory = new MantisEditorFactory();
        WindowProvider provider = mock(WindowProvider.class);
        factory.getMiniPanelContents(provider, 
                new Services(new File("tmp"), new EditorManager(Collections.<String,PluginEditorFactory<?>>emptyMap())),
                new MantisConfig());
    }
}
