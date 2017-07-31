package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.editor.testlib.VaadinTestHelper;
import com.taskadapter.web.service.Sandbox;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class JiraEditorFactoryTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void before() {
       VaadinTestHelper.initVaadinSession(getClass());
    }

    @Test
    public void miniPanelIsCreated() {
        JiraEditorFactory factory = new JiraEditorFactory();
        factory.getMiniPanelContents(new Sandbox(false, tempFolder.getRoot()), new JiraConfig(),
                new WebConnectorSetup(JiraConnector.ID(), "label1", "host", "user", "password",
                        false, "api"));
    }

    @Test
    public void serverURLIsRequiredForSave() throws BadConfigException {
        try {
            new JiraEditorFactory().validateForSave(new JiraConfig(), new WebConnectorSetup(JiraConnector.ID(), "label1",
                    "", "", "", false, ""));
            Assert.fail();
        } catch (JiraConfigException e) {
            Assert.assertTrue(e.getErrors().contains(JiraValidationErrorKind.HOST_NOT_SET));
        }
    }

    @Test
    public void projectKeyIsRequiredForSave() throws BadConfigException {
        try {
            new JiraEditorFactory().validateForSave(new JiraConfig(),
                            new WebConnectorSetup(JiraConnector.ID(), "label1", "http://somehost",
                                    "", "", false, ""));
        } catch (JiraConfigException e) {
            Assert.assertTrue(e.getErrors().contains(
                    JiraValidationErrorKind.PROJECT_NOT_SET));
        }
    }

    @Test
    public void subtasksTypeIsRequiredForSave() throws BadConfigException {
        try {
            JiraConfig config = new JiraConfig();
            config.setProjectKey("someproject");
            // clear the value
            config.setDefaultIssueTypeForSubtasks("");
            new JiraEditorFactory().validateForSave(config,
                    new WebConnectorSetup(JiraConnector.ID(), "label1", "http://somehost", "","",
                            false, ""));
            Assert.fail();
        } catch (JiraConfigException e) {
            Assert.assertTrue(e.getErrors().contains(JiraValidationErrorKind.DEFAULT_SUBTASK_TYPE_NOT_SET));
        }
    }

}
