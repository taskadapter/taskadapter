package com.taskadapter.connector.jira;

import com.atlassian.jira.rpc.soap.client.*;
import com.taskadapter.connector.common.TestUtils;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.SyncResult;
import com.taskadapter.connector.definition.TaskErrors;
import com.taskadapter.connector.definition.TaskSaveResult;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor.FIELD;
import com.taskadapter.model.GUser;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.*;

import static org.junit.Assert.*;

public class JiraTest {
    private static final Logger logger = LoggerFactory.getLogger(JiraTest.class);

    private static final String TEST_PROPERTIES = "jira.properties";
    private static JiraConfig config = new JiraConfig();
    private static WebServerInfo serverInfo;
    private static Properties properties = new Properties();
    
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
        } catch (IOException e) {
            logger.error("error loading jira test properties. " + e.getMessage(), e);
        }
    }

    @BeforeClass
    public static void oneTimeSetUp() {
        logger.info("Running Jira tests using: " + properties.getProperty("host"));
    }

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
    public void nullPriorityStaysNullAfterConversion() throws MalformedURLException, RemoteException {
        RemoteIssue issue = new RemoteIssue();
        issue.setId("123");
        issue.setKey("key");
        issue.setSummary("summary text");
        issue.setPriority(null);

        JiraTaskConverter converter = new JiraTaskConverter(config);
        GTask task = converter.convertToGenericTask(issue);
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
        RemoteIssue issue = converter.convertToJiraIssue(versions, components, task);
        //priority must be null if we don't convert priority field
        Assert.assertNull(issue.getPriority());

        //restore old mapping object for priority field
        applyStore(config, FIELD.PRIORITY, mapping);
    }

    @Test
    public void priorityExported() throws MalformedURLException, RemoteException {
        JiraConnection connection = JiraConnectionFactory.createConnection(config.getServerInfo());
        RemotePriority[] priorities = connection.getPriorities();
        if (priorities.length == 0) {
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
            issue.setPriority(priorities[0].getId());

            //save old mapping object for priority field
            MappingStore mapping = getStore(config, FIELD.PRIORITY);
            config.getFieldMappings().setMapping(FIELD.PRIORITY, true, null);

            GTask task = converter.convertToGenericTask(issue);
            RemoteIssue newIssue = converter.convertToJiraIssue(versions, components, task);
            Assert.assertEquals(issue.getPriority(), newIssue.getPriority());

            //restore old mapping object for priority field
            applyStore(config, FIELD.PRIORITY, mapping);
        }
    }

    private boolean beforeIssueTypeTest(GTask task, JiraTaskConverter converter) throws MalformedURLException, RemoteException {
        JiraConnection connection = JiraConnectionFactory.createConnection(config.getServerInfo());
        RemoteIssueType[] issueTypeList = connection.getIssueTypeList(config.getProjectKey());
        if (issueTypeList.length == 0) {
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
	 * @param config used config.
	 * @param field used field.
	 * @param store new mapping store.
	 */
	private void applyStore(JiraConfig config, FIELD field, MappingStore store) {
		Mappings mappings = config.getFieldMappings();
		if (store == null)
			mappings.deleteMappingFor(field);
		else
			mappings.setMapping(field, store.checked, store.mappedTo);
	}

    @Test
    public void issueTypeDefaultValue() throws MalformedURLException, RemoteException {
        GTask task = new GTask();
        JiraTaskConverter converter = new JiraTaskConverter(config);

        if (beforeIssueTypeTest(task, converter)) {
            task.setType(null);

            //save old mapping object for issue type field
            MappingStore mapping = getStore(config, FIELD.TASK_TYPE);
            config.getFieldMappings().setMapping(FIELD.TASK_TYPE, true, null);

            RemoteVersion[] versions = {};
            RemoteComponent[] components = {};
            RemoteIssue issue = converter.convertToJiraIssue(versions, components, task);
            //priority must be default issue type if we set the field to null
            Assert.assertEquals(config.getDefaultTaskType(), converter.getIssueTypeNameById(issue.getType()));

            //restore old mapping object for issue type field
            applyStore(config, FIELD.TASK_TYPE, mapping);
        }
    }

    @Test
    public void issueTypeExported() throws MalformedURLException, RemoteException {
        GTask task = new GTask();
        JiraTaskConverter converter = new JiraTaskConverter(config);

        if (beforeIssueTypeTest(task, converter)) {
            task.setType(converter.getIssueTypeList()[0].getName());

            //save old mapping object for issue type field
            MappingStore mapping = getStore(config, FIELD.TASK_TYPE);
            config.getFieldMappings().setMapping(FIELD.TASK_TYPE, true, null);

            RemoteVersion[] versions = {};
            RemoteComponent[] components = {};
            RemoteIssue issue = converter.convertToJiraIssue(versions, components, task);
            //priority must be default issue type if we set the field to null
            Assert.assertEquals(task.getType(), converter.getIssueTypeNameById(issue.getType()));

            //restore old mapping object for issue type field
            applyStore(config, FIELD.TASK_TYPE, mapping);
        }
    }

    public JiraConfig getTestConfig() {
        return config;
    }

}
