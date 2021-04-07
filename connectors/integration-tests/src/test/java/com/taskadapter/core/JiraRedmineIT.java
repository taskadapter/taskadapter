package com.taskadapter.core;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.definition.SaveResult;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.jira.JiraConfig;
import com.taskadapter.connector.jira.JiraConnector;
import com.taskadapter.connector.redmine.RedmineConfig;
import com.taskadapter.connector.redmine.RedmineConnector;
import com.taskadapter.connector.testlib.TestSaver;
import com.taskadapter.connector.testlib.TestUtils;
import com.taskadapter.integrationtests.RedmineTestInitializer;
import com.taskadapter.integrationtests.TestConfigs;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskBuilder;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Project;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static com.taskadapter.core.JavaFieldAdapter.AssigneeLoginNameOpt;
import static com.taskadapter.core.JavaFieldAdapter.ReporterLoginNameOpt;
import static com.taskadapter.core.JavaFieldAdapter.descriptionOpt;
import static com.taskadapter.core.JavaFieldAdapter.summaryOpt;
import static org.assertj.core.api.Assertions.assertThat;

public class JiraRedmineIT {

    private RedmineConfig sourceConfig = TestConfigs.getRedmineConfig();
    private RedmineConfig targetConfig = TestConfigs.getRedmineConfig();
    private RedmineConnector sourceRedmineConnector = new RedmineConnector(sourceConfig, TestConfigs.getRedmineSetup());
    private RedmineConnector targetRedmineConnector = new RedmineConnector(targetConfig, TestConfigs.getRedmineSetup());

    private Adapter adapter = new Adapter(sourceRedmineConnector, targetRedmineConnector);

    private RedmineConfig redmineConfigWithResolveAssignees = TestConfigs.getRedmineConfig();
    private RedmineConnector redmineConnectorWithResolveAssignees = new RedmineConnector(redmineConfigWithResolveAssignees, TestConfigs.getRedmineSetup());
    private Project redmineProject;

    private JiraConfig jiraConfig = TestConfigs.getJiraConfig();
    private WebConnectorSetup jiraSetup = TestConfigs.getJiraSetup();
    private JiraConnector jiraConnector = new JiraConnector(jiraConfig, jiraSetup);

    @Before
    public void beforeEachTest() {
        redmineConfigWithResolveAssignees.setFindUserByName(true);

        // have to create a project for each test, otherwise stuff created during one test interferes with others
        redmineProject = RedmineTestInitializer.createProject();
        sourceConfig.setProjectKey(redmineProject.getIdentifier());
        targetConfig.setProjectKey(redmineProject.getIdentifier());
        redmineConfigWithResolveAssignees.setProjectKey(redmineProject.getIdentifier());
    }

    @After
    public void afterEachTest() throws RedmineException {
        RedmineTestInitializer.deleteProject(redmineProject.getIdentifier());
    }

    // Description from Jira is saved to description in Redmine
    @Test
    public void descriptionFromJiraIsSavedToRedmine() throws ConnectorException {
        List<FieldRow<?>> rows = Arrays.asList(
                new FieldRow(summaryOpt, summaryOpt, ""),
                new FieldRow(descriptionOpt, descriptionOpt, "")
        );
        GTask task = new GTask();
        task.setValue(AllFields.summary, "summary1");
        task.setValue(AllFields.description, "description1");
        new TestSaver(redmineConnectorWithResolveAssignees, rows).saveAndLoad(task);

        SaveResult result = adapter.adapt(rows);

        GTask redmine = TestUtils.loadCreatedTask(redmineConnectorWithResolveAssignees, rows, result);
        assertThat(redmine.getValue(AllFields.description)).isEqualTo("description1");
    }

    // assignee and reporter can be loaded from JIRA and saved to Redmine
    @Test
    public void assigneeAndReporterCanBeLoadedFromJiraAndSavedToRedmine() throws ConnectorException {
        List<FieldRow<?>> rows = Arrays.asList(new FieldRow(summaryOpt, summaryOpt, ""),
                new FieldRow(AssigneeLoginNameOpt, AssigneeLoginNameOpt, null),
                new FieldRow(ReporterLoginNameOpt, ReporterLoginNameOpt, null)
        );

        GTask fromJira = TestUtils.saveAndLoad(jiraConnector,
                new GTaskBuilder()
                        .withRandom(AllFields.summary)
                        .withAssigneeLogin(RedmineTestInitializer.currentUser.getLoginName())
                        .build(),
                rows);

        GTask redmineResult = TestUtils.saveAndLoad(redmineConnectorWithResolveAssignees, fromJira, rows);

        assertThat(redmineResult.getValue(AllFields.assigneeFullName)).isEqualTo("Redmine Admin");
        assertThat(redmineResult.getValue(AllFields.assigneeLoginName)).isEqualTo("user");

        assertThat(redmineResult.getValue(AllFields.reporterFullName)).isEqualTo("Redmine Admin");
        assertThat(redmineResult.getValue(AllFields.reporterLoginName)).isEqualTo("user");
    }

    // assignee can be loaded from Redmine and saved to JIRA
    @Test
    public void assigneeCanBeLoadedFromRedmineAndSavedToJira() throws Exception {
        Issue created = RedmineTestUtil.createIssueInRedmine(redmineProject, "some description",
                RedmineTestInitializer.currentUser);
        List<GTask> loadedTasks = TaskLoader.loadTasks(1, redmineConnectorWithResolveAssignees, "sourceName", ProgressMonitorUtils.DUMMY_MONITOR);
        assertThat(loadedTasks).hasSize(1);
        GTask redmineTask = loadedTasks.get(0);
        assertThat(redmineTask.getValue(AllFields.assigneeLoginName))
                .isEqualTo(RedmineTestInitializer.currentUser.getLoginName());

        GTask result = TestUtils.saveAndLoad(jiraConnector, redmineTask,
                Arrays.asList(
                        new FieldRow(summaryOpt, summaryOpt, ""),
                        new FieldRow(AssigneeLoginNameOpt, AssigneeLoginNameOpt, null)
                )
        );
        assertThat(result.getValue(AllFields.assigneeLoginName)).isEqualTo(jiraSetup.getUserName());
        assertThat(result.getValue(AllFields.reporterFullName)).isEqualTo(jiraSetup.getUserName());
    }
}
