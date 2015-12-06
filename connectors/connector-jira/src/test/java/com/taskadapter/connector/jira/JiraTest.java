package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueLink;
import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.util.concurrent.Promise;
import com.google.common.collect.Iterables;
import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.testlib.CommonTests;
import com.taskadapter.connector.testlib.TestMappingUtils;
import com.taskadapter.connector.testlib.TestUtils;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GUser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.taskadapter.connector.jira.JiraSupportedFields.SUPPORTED_FIELDS;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class JiraTest {
    private static final Logger logger = LoggerFactory.getLogger(JiraTest.class);

    private JiraConfig config;
    private static JiraRestClient client;
    private JiraConnector connector;

    @BeforeClass
    public static void oneTimeSetUp() throws ConnectorException {
        final WebServerInfo serverInfo = new JiraTestData().getTestServerInfo();
        client = JiraConnectionFactory.createClient(serverInfo);
        logger.info("Running JIRA tests using: " + serverInfo.getHost());
    }

    @Before
    public void beforeEachTest() {
        config = new JiraTestData().createTestConfig();
        connector = new JiraConnector(config);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void doesNotFailWithNULLMonitorAndEmptyList() throws ConnectorException {
        connector.saveData(new ArrayList<>(), null, TestMappingUtils.fromFields(SUPPORTED_FIELDS));
    }

    @Test
    public void twoTasksAreCreated() throws Exception {
        CommonTests.testCreates2Tasks(connector, TestMappingUtils.fromFields(SUPPORTED_FIELDS));
    }

    @Test
    public void assigneeHasFullName() throws Exception {
        Promise<User> userPromise = client.getUserClient().getUser(config.getServerInfo().getUserName());
        final User jiraUser = userPromise.claim();

        GTask task = TestUtils.generateTask();
        task.setAssignee(new GUser(jiraUser.getName()));

        GTask loadedTask = TestUtils.saveAndLoad(connector, task, TestMappingUtils.fromFields(SUPPORTED_FIELDS));
        assertEquals(jiraUser.getName(), loadedTask.getAssignee().getLoginName());
        assertEquals(jiraUser.getDisplayName(), loadedTask.getAssignee().getDisplayName());

        TestJiraClientHelper.deleteTasks(client, loadedTask.getKey());
    }

    @Test
    public void taskUpdatedOK() throws Exception {
        CommonTests.taskCreatedAndUpdatedOK(connector, SUPPORTED_FIELDS);
    }

    @Test
    public void testGetIssuesByProject() throws Exception {
        int tasksQty = 2;
        List<GTask> tasks = TestUtils.generateTasks(tasksQty);
        connector.saveData(tasks, ProgressMonitorUtils.DUMMY_MONITOR, TestMappingUtils.fromFields(SUPPORTED_FIELDS));
        final String jql = JqlBuilder.findIssuesByProject(config.getProjectKey());
        final Iterable<Issue> issues = JiraClientHelper.findIssues(client, jql);
        assertThat(Iterables.size(issues)).isGreaterThan(1);
    }

    @Test
    public void twoIssuesLinked() throws ConnectorException {
        config.setSaveIssueRelations(true);
        List<GTask> list = new ArrayList<>();

        GTask task1 = TestUtils.generateTask();
        task1.setId(1);
        task1.setSummary("task 1 " + Calendar.getInstance().getTimeInMillis());

        GTask task2 = TestUtils.generateTask();
        task2.setId(2);
        task2.setSummary("task 2 " + Calendar.getInstance().getTimeInMillis());

        task1.getRelations().add(new GRelation(task1.getId().toString(), task2.getId().toString(), GRelation.TYPE.precedes));

        list.add(task1);
        list.add(task2);

        TestUtils.saveAndLoadList(connector, list, TestMappingUtils.fromFields(SUPPORTED_FIELDS));
        final Iterable<Issue> issues = TestJiraClientHelper.findIssuesBySummary(client, task1.getSummary());

        final Issue createdIssue1 = issues.iterator().next();
        Iterable<IssueLink> links = createdIssue1.getIssueLinks();
        assertEquals(1, Iterables.size(links));
        IssueLink link = links.iterator().next();
        String targetIssueKey = link.getTargetIssueKey();

        Issue createdIssue2 = TestJiraClientHelper.findIssuesBySummary(client, task2.getSummary()).iterator().next();
        assertEquals(createdIssue2.getKey(), targetIssueKey);

        TestJiraClientHelper.deleteTasks(client, createdIssue1.getKey(), createdIssue2.getKey());
    }
}
