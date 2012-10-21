package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.ProjectNotSetException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.connector.testlib.FileBasedTest;
import com.taskadapter.web.PluginEditorFactory;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.service.EditorManager;
import com.taskadapter.web.service.Services;
import org.junit.Test;

import java.util.Collections;

import static org.mockito.Mockito.mock;

public class JiraEditorFactoryTest extends FileBasedTest {
    @Test
    public void miniPanelIsCreated() {
        JiraEditorFactory factory = new JiraEditorFactory();
        WindowProvider provider = mock(WindowProvider.class);
        factory.getMiniPanelContents(provider,
                new Services(tempFolder, new EditorManager(Collections.<String, PluginEditorFactory<?>>emptyMap())),
                new JiraConfig());
    }

    @Test(expected = ServerURLNotSetException.class)
    public void serverURLIsRequiredForSave() throws BadConfigException {
        new JiraEditorFactory().validateForSave(new JiraConfig());
    }

    @Test(expected = ProjectNotSetException.class)
    public void projectKeyIsRequired() throws BadConfigException {
        JiraConfig config = new JiraConfig();
        config.setServerInfo(new WebServerInfo("http://somehost","",""));
        new JiraEditorFactory().validateForSave(config);
    }

}
