package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.taskadapter.connector.TestFieldBuilder;
import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.testlib.FieldWithValue;
import com.taskadapter.connector.testlib.ITFixture;
import com.taskadapter.connector.testlib.TestUtils;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GRelationType;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JiraTest {
    private static final Logger logger = LoggerFactory.getLogger(JiraTestJava.class);

    private static final JiraConfig config = JiraPropertiesLoader.createTestConfig();
    private static final WebConnectorSetup setup = JiraPropertiesLoader.getTestServerInfo();
    private static final JiraConnector connector = new JiraConnector(config, setup);

    private final ITFixture fixture;
    private final JiraRestClient client;

    public JiraTest() throws ConnectorException {
        logger.info("Running JIRA tests using: " + setup.getHost());
        client = JiraConnectionFactory.createClient(setup);
        fixture = new ITFixture(setup.getHost(), connector, id -> {
            TestJiraClientHelper.deleteTasks(client, id);
            return null;
        });
    }


    @Test
    public void assigneeAndReporterAreSetOnCreate() throws ConnectorException {
        var userPromise = client.getUserClient().getUser(setup.getUserName());
        var jiraUser = userPromise.claim();
        var task = new GTask();
        task.setValue(AllFields.summary, "some");
        task.setValue(AllFields.assigneeLoginName, jiraUser.getName());

        task.setValue(AllFields.reporterLoginName, jiraUser.getName());
        var loadedTask = TestUtils.saveAndLoad(connector, task, TestFieldBuilder.getSummaryAndAssigneeLogin());
        assertThat(loadedTask.getValue(AllFields.assigneeLoginName))
                .isEqualTo(jiraUser.getName());
        assertThat(loadedTask.getValue(AllFields.assigneeFullName))
                .isEqualTo(jiraUser.getDisplayName());

        assertThat(loadedTask.getValue(AllFields.reporterLoginName))
                .isEqualTo(jiraUser.getName());
        assertThat(loadedTask.getValue(AllFields.reporterFullName))
                .isEqualTo(jiraUser.getDisplayName());

        TestJiraClientHelper.deleteTasks(client, loadedTask.getIdentity());
    }

    @Test
    public void statusIsSetOnCreate() {
        fixture.taskIsCreatedAndLoaded(
                GTaskBuilder.withSummary().setValue(AllFields.taskStatus, "In Progress"),
                java.util.Arrays.asList(AllFields.summary, AllFields.taskStatus)
        );
    }

    @Test
    public void fieldsAreUpdated() {
        fixture.taskCreatedAndUpdatedOK(GTaskBuilder.gtaskWithRandom(AllFields.summary),
                java.util.List.of(new FieldWithValue(AllFields.taskStatus, "In Progress"),
                        new FieldWithValue(AllFields.summary, "new value"),
                        new FieldWithValue(AllFields.description, "new description")
                )
        );
    }

    @Test
    public void twoIssuesLinked() throws ConnectorException {
        config.setSaveIssueRelations(true);
        var list = generateTasks();
        var task1 = list.get(0);
        var task2 = list.get(1);
        task1.getRelations().add(new GRelation(new TaskId(task1.getId(), task1.getKey()),
                new TaskId(task2.getId(), task2.getKey()), GRelationType.precedes));
        TestUtils.saveAndLoadList(connector, list, TestFieldBuilder.getSummaryAndAssigneeLogin());
        var issues = TestJiraClientHelper.findIssuesBySummary(client, task1.getValue(AllFields.summary));
        var createdIssue1 = issues.iterator().next();
        var links = createdIssue1.getIssueLinks();
        assertThat(links).hasSize(1);
        var link = links.iterator().next();
        var targetIssueKey = link.getTargetIssueKey();
        var createdIssue2 = TestJiraClientHelper.findIssuesBySummary(client, task2.getValue(AllFields.summary))
                .iterator().next();
        assertThat(targetIssueKey).isEqualTo(createdIssue2.getKey());
        TestJiraClientHelper.deleteTasks(client,
                new TaskId(createdIssue1.getId(), createdIssue1.getKey()),
                new TaskId(createdIssue2.getId(), createdIssue2.getKey()));
    }

    private static List<GTask> generateTasks() {
        return List.of(GTaskBuilder.withSummary().setId(1L).setKey("1"),
                GTaskBuilder.withSummary().setId(2L).setKey("2"));
    }
}
