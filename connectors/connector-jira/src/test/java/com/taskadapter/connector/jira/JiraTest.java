package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.IssueRestClient;
import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.NullProgressMonitor;
import com.atlassian.jira.rest.client.RestClientException;
import com.atlassian.jira.rest.client.domain.*;
import com.atlassian.jira.rest.client.domain.input.IssueInput;
import com.atlassian.jira.rest.client.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.internal.jersey.JerseyJiraRestClient;
import com.atlassian.jira.rest.client.internal.jersey.JerseyJiraRestClientFactory;
import com.atlassian.jira.rest.client.internal.json.CommonIssueJsonParser;
import com.atlassian.jira.rest.client.internal.json.IssueJsonParser;
import com.atlassian.jira.rest.client.internal.json.JsonParseUtil;
import com.atlassian.jira.rpc.soap.client.*;
import com.google.common.collect.Iterables;
import com.taskadapter.connector.common.TestUtils;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.SyncResult;
import com.taskadapter.connector.definition.TaskErrors;
import com.taskadapter.connector.definition.TaskSaveResult;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor.FIELD;
import com.taskadapter.model.GUser;
import junit.framework.Assert;
import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.*;

import static org.junit.Assert.*;

public class JiraTest {
    private static final Logger logger = LoggerFactory.getLogger(JiraTest.class);

    private static final String TEST_PROPERTIES = "jira.properties";
    private static JiraConfig config = new JiraConfig();
    private static WebServerInfo serverInfo;
    private static Properties properties = new Properties();
    private static JiraConnection connection;

    private static class MappingStore {
        final boolean checked;
        final String mappedTo;

        MappingStore(boolean checked, String mappedTo) {
            super();
            this.checked = checked;
            this.mappedTo = mappedTo;
        }
    }

    static {
        InputStream is = JiraTest.class.getClassLoader().getResourceAsStream(
                TEST_PROPERTIES);
        if (is == null) {
            throw new RuntimeException("Can't find file " + TEST_PROPERTIES
                    + " in classpath.");
        }
        try {
            properties.load(is);
            serverInfo = new WebServerInfo(properties.getProperty("host"),
                    properties.getProperty("login"),
                    properties.getProperty("password"));
            config.setServerInfo(serverInfo);
            config.setProjectKey(properties.getProperty("project.key"));

            connection = JiraConnectionFactory.createConnection(config.getServerInfo());
        } catch (IOException e) {
            logger.error("error loading jira test properties. " + e.getMessage(), e);
        } catch (URISyntaxException e) {
            logger.error("error creating jira connector. " + e.getMessage(), e);
        }
    }

    @BeforeClass
    public static void oneTimeSetUp() {
        logger.info("Running Jira tests using: " + properties.getProperty("host"));
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
        RemoteUser jiraUser = connection.getUser(config.getServerInfo().getUserName());

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
            assertEquals(jiraUser.getFullname(), loaded.getAssignee().getDisplayName());
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
    public void testDeleteIssue() throws ConnectorException, MalformedURLException, URISyntaxException, RemoteException {
        int tasksQty = 1;
        GTask task = TestUtils.generateTask();

        Integer id = task.getId();

        // CREATE
        JiraConnector connector = new JiraConnector(config);
        SyncResult<TaskSaveResult, TaskErrors<Throwable>> result = connector.saveData(Arrays.asList(task), null);
        assertTrue(result.getErrors().getErrors().isEmpty());
        assertEquals(tasksQty, result.getResult().getCreatedTasksNumber());
        String remoteKey = result.getResult().getRemoteKey(id);

        Issue loaded = connection.getIssueByKey(remoteKey);
        connection.deleteIssue(loaded.getKey(), false);

        thrown.expect(RestClientException.class);
        thrown.expectMessage("Issue Does Not Exist");

        connection.getIssueByKey(remoteKey);
    }

    @Test
    public void nullPriorityStaysNullAfterConversion() throws MalformedURLException, RemoteException {
        IssueInputBuilder issueInputBuilder = new IssueInputBuilder(config.getProjectKey(), new Long(1));
        issueInputBuilder.setSummary("summary text");
        issueInputBuilder.setPriority(null);

        JiraTaskConverter converter = new JiraTaskConverter(config);
        GTask task = null;//converter.convertToGenericTask(issueInputBuilder.build());
        //priority must be null cause we need to save original issue priority value
        Assert.assertNull(task.getPriority());
    }

    @Test
    public void priorityNotExported() throws MalformedURLException, RemoteException {
        GTask task = new GTask();
        task.setId(1);
        task.setKey("1");
        task.setSummary("summary text");
        task.setPriority(null);

        //save old mapping object for priority field
        MappingStore mapping = getStore(config, FIELD.PRIORITY);
        config.getFieldMappings().setMapping(FIELD.PRIORITY, false, null);

        RemoteVersion[] versions = {};
        RemoteComponent[] components = {};
        JiraTaskConverter converter = new JiraTaskConverter(config);
        IssueInput issue = converter.convertToJiraIssue(versions, components, task);
        //priority must be null if we don't convert priority field
        Assert.assertNull(issue.getField("priority").getValue());

        //restore old mapping object for priority field
        applyStore(config, FIELD.PRIORITY, mapping);
    }

    @Test
    public void priorityExported() throws MalformedURLException, RemoteException, URISyntaxException {
        JiraConnection connection = JiraConnectionFactory.createConnection(config.getServerInfo());
        Iterable<Priority> priorities = connection.getPriorities();
        if (Iterables.size(priorities) == 0) {
            logger.info("Can't test priority field export - priority list is empty.");
        } else {
            RemoteVersion[] versions = {};
            RemoteComponent[] components = {};
            JiraTaskConverter converter = new JiraTaskConverter(config);

            converter.setPriorities(priorities);
            RemoteIssue issue = new RemoteIssue();
            issue.setId("123");
            issue.setKey("key");
            issue.setSummary("summary text");
            issue.setPriority(String.valueOf(Iterables.get(priorities, 0).getId()));

            //save old mapping object for priority field
            MappingStore mapping = getStore(config, FIELD.PRIORITY);
            config.getFieldMappings().setMapping(FIELD.PRIORITY, true, null);

            GTask task = null;//converter.convertToGenericTask(issue);
            IssueInput newIssue = converter.convertToJiraIssue(versions, components, task);
            Assert.assertEquals(issue.getPriority(), newIssue.getField("priority").getValue());

            //restore old mapping object for priority field
            applyStore(config, FIELD.PRIORITY, mapping);
        }
    }

    private boolean beforeIssueTypeTest(GTask task, JiraTaskConverter converter) throws MalformedURLException, RemoteException, URISyntaxException {
        JiraConnection connection = JiraConnectionFactory.createConnection(config.getServerInfo());
        Iterable<IssueType> issueTypeList = connection.getIssueTypeList();
        if (Iterables.size(issueTypeList) == 0) {
            logger.info("Can't test issue type field export - issue type list is empty.");
            return false;
        } else {
            converter.setIssueTypeList(issueTypeList);

            task.setId(1);
            task.setKey("1");
            task.setSummary("summary text");

            return true;
        }
    }

    private MappingStore getStore(JiraConfig config, FIELD field) {
        Mappings mappings = config.getFieldMappings();
        if (!mappings.haveMappingFor(field))
            return null;
        return new MappingStore(mappings.isFieldSelected(field), config.getFieldMappings().getMappedTo(field));
    }

    /**
     * Applies a mapping store.
     *
     * @param config used config.
     * @param field  used field.
     * @param store  new mapping store.
     */
    private void applyStore(JiraConfig config, FIELD field, MappingStore store) {
        Mappings mappings = config.getFieldMappings();
        if (store == null)
            mappings.deleteMappingFor(field);
        else
            mappings.setMapping(field, store.checked, store.mappedTo);
    }

    @Test
    public void issueTypeDefaultValue() throws MalformedURLException, RemoteException, URISyntaxException {
        GTask task = new GTask();
        JiraTaskConverter converter = new JiraTaskConverter(config);

        if (beforeIssueTypeTest(task, converter)) {
            task.setType(null);

            //save old mapping object for issue type field
            MappingStore mapping = getStore(config, FIELD.TASK_TYPE);
            config.getFieldMappings().setMapping(FIELD.TASK_TYPE, true, null);

            RemoteVersion[] versions = {};
            RemoteComponent[] components = {};
            IssueInput issue = converter.convertToJiraIssue(versions, components, task);
            //priority must be default issue type if we set the field to null
            Assert.assertEquals(config.getDefaultTaskType(), converter.getIssueTypeNameById(issue.getField("issueType").getId()));

            //restore old mapping object for issue type field
            applyStore(config, FIELD.TASK_TYPE, mapping);
        }
    }

    @Test
    public void issueTypeExported() throws MalformedURLException, RemoteException, URISyntaxException {
        GTask task = new GTask();
        JiraTaskConverter converter = new JiraTaskConverter(config);

        if (beforeIssueTypeTest(task, converter)) {
            task.setType(Iterables.get(converter.getIssueTypeList(), 0).getName());

            //save old mapping object for issue type field
            MappingStore mapping = getStore(config, FIELD.TASK_TYPE);
            config.getFieldMappings().setMapping(FIELD.TASK_TYPE, true, null);

            RemoteVersion[] versions = {};
            RemoteComponent[] components = {};
            IssueInput issue = converter.convertToJiraIssue(versions, components, task);
            //priority must be default issue type if we set the field to null
            Assert.assertEquals(task.getType(), converter.getIssueTypeNameById(issue.getField("issueType").getId()));

            //restore old mapping object for issue type field
            applyStore(config, FIELD.TASK_TYPE, mapping);
        }
    }

    public JiraConfig getTestConfig() {
        return config;
    }

    @Test
    public void testGetIssuesByProject() throws Exception {
        int tasksQty = 2;
        List<GTask> tasks = TestUtils.generateTasks(tasksQty);
        JiraConnector connector = new JiraConnector(config);
        SyncResult<TaskSaveResult, TaskErrors<Throwable>> result = connector.saveData(tasks, null);

        Iterable<Issue> issues = connection.getIssuesByProject(config.getProjectKey());
        Assert.assertNotSame(0, Iterables.size(issues));
    }

}
