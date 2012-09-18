package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.RestClientException;
import com.atlassian.jira.rest.client.domain.*;
import com.atlassian.jira.rest.client.domain.input.IssueInputBuilder;
import com.atlassian.jira.rpc.soap.client.RemoteUser;
import com.google.common.collect.Iterables;
import com.taskadapter.connector.common.TestUtils;
import com.taskadapter.connector.definition.SyncResult;
import com.taskadapter.connector.definition.TaskErrors;
import com.taskadapter.connector.definition.TaskSaveResult;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GUser;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.*;

import static org.junit.Assert.*;

public class JiraTest {
    private static final Logger logger = LoggerFactory.getLogger(JiraTest.class);

    private JiraConfig config;
    private static WebServerInfo serverInfo;
    private static JiraConnection connection;

    @BeforeClass
    public static void oneTimeSetUp() {
        serverInfo = new JiraTestData().getTestServerInfo();
        logger.info("Running Jira tests using: " + serverInfo.getHost());
        try {
            connection = JiraConnectionFactory.createConnection(serverInfo);
        } catch (Exception e) {
            fail("Can't init Jira tests: " + e.toString());
        }
    }

    @Before
    public void beforeEachTest() {
        config = new JiraTestData().createTestConfig();
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    // TODO this is identical to Redmine's test. maybe move to a common place?
    @Test
    public void testJiraDoesNotFailWithNULLMonitorAndEmptyList()
            throws Exception {
        JiraConnector connector = new JiraConnector(config);
        connector.saveData(new ArrayList<GTask>(), null);
    }

    @Test
    public void twoTasksAreCreated() throws Exception {
        int tasksQty = 2;
        List<GTask> tasks = TestUtils.generateTasks(tasksQty);
        JiraConnector connector = new JiraConnector(config);
        SyncResult<TaskSaveResult, TaskErrors<Throwable>> result = connector.saveData(tasks, null);
        assertEquals(tasksQty, result.getResult().getCreatedTasksNumber());
    }

    @Test
    public void assigneeHasFullName() throws Exception {
        JiraConnector connector = new JiraConnector(config);
        JiraConnection connection = JiraConnectionFactory.createConnection(config.getServerInfo());
        User jiraUser = connection.getUser(config.getServerInfo().getUserName());

        List<GTask> tasks = TestUtils.generateTasks(1);
        tasks.get(0).setAssignee(new GUser(jiraUser.getName()));

        SyncResult<TaskSaveResult, TaskErrors<Throwable>> result = connector.saveData(tasks, null);
        assertFalse("Task creation failed", result.getErrors().hasErrors());

        Map<Integer, String> remoteKeyById = new HashMap<Integer, String>();
        for (GTask task : tasks) {
            remoteKeyById.put(task.getId(), result.getResult().getIdToRemoteKeyMap().get(task.getId()));
        }

        for (Map.Entry<Integer, String> entry : remoteKeyById.entrySet()) {
            GTask loaded = connector.loadTaskByKey(serverInfo, entry.getValue());
            assertEquals(jiraUser.getName(), loaded.getAssignee().getLoginName());
            assertEquals(jiraUser.getDisplayName(), loaded.getAssignee().getDisplayName());
        }
    }

    // see http://www.hostedredmine.com/issues/41212
    @Test
    public void issueUpdatedOK() throws Exception {
        int tasksQty = 1;
        GTask task = TestUtils.generateTask();

        Integer id = task.getId();

        // CREATE
        JiraConnector connector = new JiraConnector(config);
        SyncResult<TaskSaveResult, TaskErrors<Throwable>> result = connector.saveData(Arrays.asList(task), null);
        assertTrue(result.getErrors().getErrors().isEmpty());
        assertEquals(tasksQty, result.getResult().getCreatedTasksNumber());
        String remoteKey = result.getResult().getRemoteKey(id);

        GTask loaded = connector.loadTaskByKey(serverInfo, remoteKey);

        // UPDATE
        String NEW_SUMMARY = "new summary here";
        loaded.setSummary(NEW_SUMMARY);
        loaded.setRemoteId(remoteKey);
        SyncResult<TaskSaveResult, TaskErrors<Throwable>> result2 = connector.saveData(Arrays.asList(loaded), null);
        assertTrue("some errors while updating the data: " + result2.getErrors(), result2.getErrors().getErrors().isEmpty());
        assertEquals(1, result2.getResult().getUpdatedTasksNumber());

        GTask loadedAgain = connector.loadTaskByKey(serverInfo, remoteKey);
        assertEquals(NEW_SUMMARY, loadedAgain.getSummary());

    }

    @Test
    public void testGetIssuesByProject() throws Exception {
        int tasksQty = 2;
        List<GTask> tasks = TestUtils.generateTasks(tasksQty);
        JiraConnector connector = new JiraConnector(config);
        connector.saveData(tasks, null);

        Iterable<Issue> issues = connection.getIssuesByProject(config.getProjectKey());
        Assert.assertNotSame(0, Iterables.size(issues));
    }

    @Test
    public void twoIssuesLinked() throws ConnectorException {
        config.setSaveIssueRelations(true);
        JiraConnector connector = new JiraConnector(config);
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

        TestUtils.saveAndLoadList(connector, list);
        List<Issue> issues = connection.getIssuesBySummary(task1.getSummary());
        Issue issue2 = connection.getIssuesBySummary(task2.getSummary()).get(0);

        Iterable<IssueLink> links = issues.get(0).getIssueLinks();
        assertEquals(1, Iterables.size(links));
        IssueLink link = links.iterator().next();
        String targetIssueKey = link.getTargetIssueKey();
        assertEquals(issue2.getKey(), targetIssueKey);
    }

}
