package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.domain.BasicComponent;
import com.atlassian.jira.rest.client.domain.IssueType;
import com.atlassian.jira.rest.client.domain.Priority;
import com.atlassian.jira.rest.client.domain.Version;
import com.atlassian.jira.rest.client.domain.input.ComplexIssueInputFieldValue;
import com.atlassian.jira.rest.client.domain.input.FieldInput;
import com.atlassian.jira.rest.client.domain.input.IssueInput;
import com.google.common.collect.Iterables;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;

import static org.junit.Assert.*;

public class JiraTaskConverterTest {

    private static Iterable<Priority> priorities;
    private static Iterable<IssueType> issueTypeList;
    private static Iterable<Version> versions;
    private static Iterable<BasicComponent> components;

    private JiraConfig config;

    @BeforeClass
    public static void oneTimeSetUp() {
        try {
            loadDataFromServer();
        } catch (Exception e) {
            fail("Can't init Jira tests: " + e.toString());
        }
    }

    private static void loadDataFromServer() throws RemoteException, MalformedURLException, URISyntaxException {
        JiraTestData jiraTestData = new JiraTestData();
        String projectKey = jiraTestData.getProjectKey();
        WebServerInfo serverInfo = jiraTestData.getTestServerInfo();
        JiraConnection connection = JiraConnectionFactory.createConnection(serverInfo);
        priorities = connection.getPriorities();
        if (Iterables.isEmpty(priorities)) {
            fail("Can't test priority field export - priority list is empty.");
        }

        issueTypeList = connection.getIssueTypeList();
        if (Iterables.isEmpty(issueTypeList)) {
            fail("can't find any issue types.");
        }
        versions = connection.getVersions(projectKey);
        components = connection.getComponents(projectKey);
    }

    @Before
    public void beforeEachTest() {
        config = new JiraTestData().createTestConfig();
    }

    @Test (expected = IllegalStateException.class)
    public void defaultCo() {
        JiraTaskConverter converter = new JiraTaskConverter(config);
        GTask task = new GTask();
        task.setSummary("some task");
        converter.convertToJiraIssue(task);
    }

    @Test
    public void priorityConvertedToCritical() throws MalformedURLException, RemoteException, URISyntaxException {
        Priority priorityCritical = find(priorities, "Critical");

        GTask task = new GTask();
        task.setSummary("task with critical priority");
        task.setPriority(750);

        config.getFieldMappings().setMapping(GTaskDescriptor.FIELD.PRIORITY, true, null);
        JiraTaskConverter converter = getConverter(config);

        IssueInput newIssue = converter.convertToJiraIssue(task);
        String actualPriorityId = getId(newIssue, "priority");
        assertEquals(priorityCritical.getId().toString(), actualPriorityId);
    }

    @Test
    public void priorityNotExported() throws MalformedURLException, RemoteException {
        GTask task = new GTask();
        task.setSummary("something");
        task.setPriority(700);
        config.getFieldMappings().setMapping(GTaskDescriptor.FIELD.PRIORITY, false, null);
        JiraTaskConverter converter = getConverter(config);
        IssueInput issue = converter.convertToJiraIssue(task);
        assertNull(issue.getField("priority"));
    }

    @Test
    public void issueTypeExported() throws MalformedURLException, RemoteException, URISyntaxException {
        GTask task = new GTask();
        task.setSummary("checking issueType");

        JiraTaskConverter converter = getConverter(config);
        IssueType requiredIssueType = findIssueType(issueTypeList, "Task");
        task.setType("Task");

        config.getFieldMappings().setMapping(GTaskDescriptor.FIELD.TASK_TYPE, true, null);

        IssueInput issue = converter.convertToJiraIssue(task);
        assertEquals(requiredIssueType.getId().toString(), getId(issue, "issuetype"));
    }

    @Test
    public void defaultIssueTypeSetWhenNoneProvided() throws MalformedURLException, RemoteException, URISyntaxException {
        GTask task = new GTask();
        task.setType(null);
        config.getFieldMappings().setMapping(GTaskDescriptor.FIELD.TASK_TYPE, true, null);
        JiraTaskConverter converter = getConverter(config);

        IssueInput issue = converter.convertToJiraIssue(task);
        // must be default issue type if we set the field to null
        assertEquals(findDefaultIssueTypeId(), getId(issue, "issuetype"));
    }

    private String getId(IssueInput issue, String fieldName) {
        FieldInput actualPriorityField = issue.getField(fieldName);
        ComplexIssueInputFieldValue value = (ComplexIssueInputFieldValue) actualPriorityField.getValue();
        return (String) value.getValuesMap().get("id");
    }

    private String findDefaultIssueTypeId() {
        String defaultTaskTypeName = config.getDefaultTaskType();
        IssueType issueType = findIssueType(issueTypeList, defaultTaskTypeName);
        return issueType.getId().toString();
    }

    private IssueType findIssueType(Iterable<IssueType> issueTypes, String type) {
        for (IssueType issueType : issueTypes) {
            if (issueType.getName().equals(type)) {
                return issueType;
            }
        }
        throw new RuntimeException("Not found: " + type);
    }

    private JiraTaskConverter getConverter(JiraConfig config) {
        JiraTaskConverter converter = new JiraTaskConverter(config);
        converter.setPriorities(priorities);
        converter.setIssueTypeList(issueTypeList);
        converter.setVersions(versions);
        converter.setComponents(components);
        return converter;
    }

    private Priority find(Iterable<Priority> priorities, String priorityName) {
        for (Priority priority : priorities) {
            if (priority.getName().equals(priorityName)) {
                return priority;
            }
        }
        throw new RuntimeException("Priority not found: " + priorityName);
    }
}
