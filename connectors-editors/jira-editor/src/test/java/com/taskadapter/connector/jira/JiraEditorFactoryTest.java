package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.ProjectNotSetException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.connector.testlib.FileBasedTest;
import com.taskadapter.web.WindowProvider;
import com.taskadapter.web.service.Sandbox;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class JiraEditorFactoryTest extends FileBasedTest {
    @Test
    public void miniPanelIsCreated() {
        JiraEditorFactory factory = new JiraEditorFactory();
        WindowProvider provider = mock(WindowProvider.class);
        factory.getMiniPanelContents(provider,
                new Sandbox(false, tempFolder),
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
