package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.FieldMapping;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.ProjectNotSetException;
import com.taskadapter.connector.definition.exceptions.ServerURLNotSetException;
import com.taskadapter.editor.testlib.VaadinTestHelper;
import com.taskadapter.web.service.Sandbox;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JiraEditorFactoryTest {

    private static final List<FieldMapping<?>> NO_MAPPINGS = List.of();

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void beforeEachTest() {
        VaadinTestHelper.initVaadinSession(JiraEditorFactoryTest.class);
    }

    private JiraEditorFactory factory = new JiraEditorFactory();

    @Test
    public void miniPanelIsCreated() {
        factory.getMiniPanelContents(new Sandbox(false, tempFolder.getRoot()), new JiraConfig(),
                WebConnectorSetup.apply(JiraConnector.ID(), "label1", "host", "user", "password", false, "api"));
    }

    @Test
    public void serverURLIsRequiredForSave() {
        var exceptions = factory.validateForSave(new JiraConfig(),
                WebConnectorSetup.apply(JiraConnector.ID(), "label1", "", "", "", false, ""), NO_MAPPINGS);
        assertThat(exceptions.get(0)).isInstanceOf(ServerURLNotSetException.class);
    }

    @Test
    public void projectKeyIsRequiredForSave() {
        var exceptions = factory.validateForSave(new JiraConfig(),
                WebConnectorSetup.apply(JiraConnector.ID(), "label1", "http://somehost", "", "", false, ""), NO_MAPPINGS);
        assertThat(exceptions.get(0)).isInstanceOf(ProjectNotSetException.class);
    }

    @Test
    public void subtasksTypeIsRequiredForSave() {
        var config = new JiraConfig();
        config.setProjectKey("someproject");
        // clear the value
        config.setDefaultIssueTypeForSubtasks("");
        var exceptions = factory.validateForSave(config,
                WebConnectorSetup.apply(JiraConnector.ID(), "label1", "http://somehost", "", "", false, ""), NO_MAPPINGS);
        assertThat(exceptions.get(0)).isInstanceOf(DefaultSubTaskTypeNotSetException.class);
    }
}
