package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.IssueLink;
import com.atlassian.jira.rest.client.domain.User;
import com.google.common.collect.Iterables;
import com.taskadapter.connector.definition.TaskSaveResult;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.testlib.CommonTests;
import com.taskadapter.connector.testlib.TestMappingUtils;
import com.taskadapter.connector.testlib.TestUtils;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GUser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.taskadapter.connector.jira.JiraSupportedFields.SUPPORTED_FIELDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class JiraTest {
    private static final Logger logger = LoggerFactory.getLogger(JiraTest.class);

    private JiraConfig config;
    private static WebServerInfo serverInfo;
    private static JiraConnection connection;
    private JiraConnector connector;

    @BeforeClass
    public static void oneTimeSetUp() {
        serverInfo = new JiraTestData().getTestServerInfo();
        logger.info("Running JIRA tests using: " + serverInfo.getHost());
        try {
            connection = JiraConnectionFactory.createConnection(serverInfo);
        } catch (Exception e) {
            fail("Can't init JIRA tests: " + e.toString());
        }
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
        connector.saveData(new ArrayList<GTask>(), null, TestMappingUtils.fromFields(SUPPORTED_FIELDS));
    }

    @Test
    public void twoTasksAreCreated() throws Exception {
        CommonTests.testCreates2Tasks(connector, TestMappingUtils.fromFields(SUPPORTED_FIELDS));
    }

    @Test
    public void assigneeHasFullName() throws Exception {
        JiraConnection connection = JiraConnectionFactory.createConnection(config.getServerInfo());
        User jiraUser = connection.getUser(config.getServerInfo().getUserName());

        List<GTask> tasks = TestUtils.generateTasks(1);
        tasks.get(0).setAssignee(new GUser(jiraUser.getName()));

        TaskSaveResult result = connector.saveData(tasks, null, TestMappingUtils.fromFields(SUPPORTED_FIELDS));
        assertFalse("Task creation failed", result.hasErrors());

        Map<Integer, String> remoteKeyById = new HashMap<Integer, String>();
        for (GTask task : tasks) {
            remoteKeyById.put(task.getId(), result.getIdToRemoteKeyMap().get(task.getId()));
        }

        for (Map.Entry<Integer, String> entry : remoteKeyById.entrySet()) {
            GTask loaded = connector.loadTaskByKey(serverInfo, entry.getValue());
            assertEquals(jiraUser.getName(), loaded.getAssignee().getLoginName());
            assertEquals(jiraUser.getDisplayName(), loaded.getAssignee().getDisplayName());
        }
    }

    // see http://www.hostedredmine.com/issues/41212
    @Test
    public void taskUpdatedOK() throws Exception {
        CommonTests.taskCreatedAndUpdatedOK(connector, SUPPORTED_FIELDS);
    }

    @Test
    public void testGetIssuesByProject() throws Exception {
        int tasksQty = 2;
        List<GTask> tasks = TestUtils.generateTasks(tasksQty);
        connector.saveData(tasks, null, TestMappingUtils.fromFields(SUPPORTED_FIELDS));

        Iterable<Issue> issues = connection.getIssuesByProject(config.getProjectKey());
        Assert.assertNotSame(0, Iterables.size(issues));
    }

    @Test
    public void twoIssuesLinked() throws ConnectorException {
        config.setSaveIssueRelations(true);
        List<GTask> list = new ArrayList<GTask>();

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
        List<Issue> issues = connection.getIssuesBySummary(task1.getSummary());
        Issue issue2 = connection.getIssuesBySummary(task2.getSummary()).get(0);

        Iterable<IssueLink> links = issues.get(0).getIssueLinks();
        assertEquals(1, Iterables.size(links));
        IssueLink link = links.iterator().next();
        String targetIssueKey = link.getTargetIssueKey();
        assertEquals(issue2.getKey(), targetIssueKey);
    }
}
