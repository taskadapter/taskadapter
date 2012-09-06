package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.domain.*;
import com.atlassian.jira.rest.client.domain.input.ComplexIssueInputFieldValue;
import com.atlassian.jira.rest.client.domain.input.FieldInput;
import com.atlassian.jira.rest.client.domain.input.IssueInput;
import com.google.common.collect.Iterables;
import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor;
import com.taskadapter.model.GUser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.Calendar;

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
            e.printStackTrace();
            fail("Can't init Jira tests: " + e.toString());
        }
    }

    private static void loadDataFromServer() throws IOException, URISyntaxException {
        JiraTestData jiraTestData = new JiraTestData();
        String projectKey = jiraTestData.getProjectKey();
        WebServerInfo serverInfo = jiraTestData.getTestServerInfo();
        JiraConnection connection = JiraConnectionFactory.createConnection(serverInfo);

        priorities = MockData.loadPriorities();
        if (Iterables.isEmpty(priorities)) {
            fail("Can't test priority field export - priority list is empty.");
        }

        issueTypeList = MockData.loadIssueTypes();
        if (Iterables.isEmpty(issueTypeList)) {
            fail("can't find any issue types.");
        }
        // reading Version objects fails with
        // "Failed to invoke public org.joda.time.Chronology() with no args"
//        versions = MockData.loadVersions();
        versions = connection.getVersions(projectKey);
        components = MockData.loadComponents();
    }

    @Before
    public void beforeEachTest() {
        config = new JiraTestData().createTestConfig();
    }

    @Test(expected = IllegalStateException.class)
    public void noIssueTypesSetGeneratesIllegalStateException() {
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
        JiraTaskConverter converter = getConverter();

        IssueInput newIssue = converter.convertToJiraIssue(task);
        String actualPriorityId = getId(newIssue, IssueFieldId.PRIORITY_FIELD.id);
        assertEquals(priorityCritical.getId().toString(), actualPriorityId);
    }

    @Test
    public void priorityNotExportedWhenNotSelected() throws MalformedURLException, RemoteException {
        GTask task = new GTask();
        task.setSummary("something");
        task.setPriority(700);
        JiraTaskConverter converter = createConverterWithUnselectedField(GTaskDescriptor.FIELD.PRIORITY);
        IssueInput issue = converter.convertToJiraIssue(task);
        assertNull(issue.getField(IssueFieldId.PRIORITY_FIELD.id));
    }

    @Test
    public void issueTypeExported() throws MalformedURLException, RemoteException, URISyntaxException {
        GTask task = new GTask();
        task.setSummary("checking issueType");

        JiraTaskConverter converter = getConverter();
        IssueType requiredIssueType = findIssueType(issueTypeList, "Task");
        task.setType("Task");

        config.getFieldMappings().setMapping(GTaskDescriptor.FIELD.TASK_TYPE, true, null);

        IssueInput issue = converter.convertToJiraIssue(task);
        assertEquals(requiredIssueType.getId().toString(), getId(issue, IssueFieldId.ISSUE_TYPE_FIELD.id));
    }

    @Test
    public void defaultIssueTypeSetWhenNoneProvided() throws MalformedURLException, RemoteException, URISyntaxException {
        GTask task = new GTask();
        task.setType(null);
        config.getFieldMappings().setMapping(GTaskDescriptor.FIELD.TASK_TYPE, true, null);
        JiraTaskConverter converter = getConverter();

        IssueInput issue = converter.convertToJiraIssue(task);
        // must be default issue type if we set the field to null
        assertEquals(findDefaultIssueTypeId(), getId(issue, IssueFieldId.ISSUE_TYPE_FIELD.id));
    }

    @Test
    public void summaryIsConvertedByDefault() {
        JiraTaskConverter converter = getConverter();
        GTask task = new GTask();
        String summary = "summary here";
        task.setSummary(summary);
        IssueInput issueInput = converter.convertToJiraIssue(task);
        assertEquals(summary, getValue(issueInput, IssueFieldId.SUMMARY_FIELD.id));
    }

    @Test
    public void summaryIsNotConvertedWhenNotSelected() {
        JiraTaskConverter converter = createConverterWithUnselectedField(GTaskDescriptor.FIELD.SUMMARY);
        GTask task = new GTask();
        String summary = "summary here";
        task.setSummary(summary);
        IssueInput issue = converter.convertToJiraIssue(task);
        assertNull(issue.getField(IssueFieldId.SUMMARY_FIELD.id));
    }

    @Test
    public void descriptionIsConvertedByDefault() {
        JiraTaskConverter converter = getConverter();
        GTask task = new GTask();
        task.setDescription("description here");
        IssueInput issueInput = converter.convertToJiraIssue(task);
        assertEquals("description here", getValue(issueInput, IssueFieldId.DESCRIPTION_FIELD.id));
    }

    @Test
    public void descriptionIsNOTConvertedWhenNotSelected() {
        JiraTaskConverter converter = createConverterWithUnselectedField(GTaskDescriptor.FIELD.DESCRIPTION);
        GTask task = new GTask();
        task.setDescription("description here");
        IssueInput issueInput = converter.convertToJiraIssue(task);
        assertNull(issueInput.getField(IssueFieldId.DESCRIPTION_FIELD.id));
    }

    @Test
    public void dueDateConvertedByDefault() {
        JiraTaskConverter converter = getConverter();
        GTask task = new GTask();
        Calendar calendar = Calendar.getInstance();
        calendar.set(2014, 3, 28, 0, 0, 0);
        task.setDueDate(calendar.getTime());
        IssueInput issueInput = converter.convertToJiraIssue(task);
        assertEquals("2014-04-28", getValue(issueInput, IssueFieldId.DUE_DATE_FIELD.id));
    }

    @Test
    public void dueDateNotConvertedWhenNotSelected() {
        JiraTaskConverter converter = createConverterWithUnselectedField(GTaskDescriptor.FIELD.DUE_DATE);
        GTask task = new GTask();
        Calendar calendar = Calendar.getInstance();
        calendar.set(2014, 3, 28, 0, 0, 0);
        task.setDueDate(calendar.getTime());
        IssueInput issue = converter.convertToJiraIssue(task);
        assertNull(issue.getField(IssueFieldId.DUE_DATE_FIELD.id));
    }

    @Test
    public void assigneeNotConvertedWhenNotSelected() {
        JiraTaskConverter converter = createConverterWithUnselectedField(GTaskDescriptor.FIELD.ASSIGNEE);
        GTask task = new GTask();
        task.setAssignee(new GUser("mylogin"));
        IssueInput issue = converter.convertToJiraIssue(task);
        assertNull(issue.getField(IssueFieldId.ASSIGNEE_FIELD.id));
    }

    @Test
    public void assigneeConvertedIfSelected() {
        JiraTaskConverter converter = createConverterWithSelectedField(GTaskDescriptor.FIELD.ASSIGNEE);
        GTask task = new GTask();
        task.setAssignee(new GUser("mylogin"));
        IssueInput issue = converter.convertToJiraIssue(task);
        assertEquals("mylogin", getComplexValue(issue, IssueFieldId.ASSIGNEE_FIELD.id, "name"));
    }

    private String getId(IssueInput issue, String fieldName) {
        FieldInput field = issue.getField(fieldName);
        ComplexIssueInputFieldValue value = (ComplexIssueInputFieldValue) field.getValue();
        return (String) value.getValuesMap().get("id");
    }

    private String getValue(IssueInput issue, String fieldName) {
        FieldInput field = issue.getField(fieldName);
        return (String) field.getValue();
    }

    private String getComplexValue(IssueInput issue, String fieldName, String subFieldName) {
        FieldInput field = issue.getField(fieldName);
        ComplexIssueInputFieldValue value = (ComplexIssueInputFieldValue) field.getValue();
        return (String) value.getValuesMap().get(subFieldName);
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

    private JiraTaskConverter getConverter() {
        JiraTaskConverter converter = new JiraTaskConverter(config);
        converter.setPriorities(priorities);
        converter.setIssueTypeList(issueTypeList);
        converter.setVersions(versions);
        converter.setComponents(components);
        return converter;
    }

    private JiraTaskConverter createConverterWithSelectedField(GTaskDescriptor.FIELD field) {
        return createConverterWithField(field, true);
    }

    private JiraTaskConverter createConverterWithUnselectedField(GTaskDescriptor.FIELD field) {
        return createConverterWithField(field, false);
    }

    private JiraTaskConverter createConverterWithField(GTaskDescriptor.FIELD field, boolean selected) {
        JiraConfig config = new JiraTestData().createTestConfig();
        config.getFieldMappings().setMapping(field, selected, null);
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
