package com.taskadapter.connector.jira;

import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.service.Services;
import org.junit.Test;

import java.io.File;

import static org.mockito.Mockito.mock;

public class JiraEditorFactoryTest {
    @Test
    public void miniPanelIsCreated() {
        JiraEditorFactory factory = new JiraEditorFactory();
        WindowProvider provider = mock(WindowProvider.class);
        factory.getMiniPanelContents(provider, new Services(new File("tmp")), new JiraConfig());
    }

}
