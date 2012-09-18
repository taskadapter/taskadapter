package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.domain.*;
import com.atlassian.jira.rest.client.domain.input.ComplexIssueInputFieldValue;
import com.atlassian.jira.rest.client.domain.input.FieldInput;
import com.atlassian.jira.rest.client.domain.input.IssueInput;
import com.google.common.collect.Iterables;
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

public class GTaskToJiraTest {

    private static Iterable<Priority> priorities;
    private static Iterable<IssueType> issueTypeList;
    private static Iterable<Version> versions;
    private static Iterable<BasicComponent> components;

    private JiraConfig config;

    @BeforeClass
    public static void oneTimeSetUp() {
        try {
            loadMockData();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Can't init Jira tests: " + e.toString());
        }
    }

    private static void loadMockData() throws IOException {
        priorities = MockData.loadPriorities();
        if (Iterables.isEmpty(priorities)) {
            fail("Can't test priority field export - priority list is empty.");
        }

        issueTypeList = MockData.loadIssueTypes();
        if (Iterables.isEmpty(issueTypeList)) {
            fail("can't find any issue types.");
        }
        versions = MockData.loadVersions();
        components = MockData.loadComponents();
    }

    @Before
    public void beforeEachTest() {
        config = new JiraTestData().createTestConfig();
    }

    @Test(expected = IllegalStateException.class)
    public void noIssueTypesSetGeneratesIllegalStateException() {
        GTaskToJira converter = new GTaskToJira(config);
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
        GTaskToJira converter = getConverter();

        IssueInput newIssue = converter.convertToJiraIssue(task);
        String actualPriorityId = getId(newIssue, IssueFieldId.PRIORITY_FIELD.id);
        assertEquals(priorityCritical.getId().toString(), actualPriorityId);
    }

    @Test
    public void priorityNotExportedWhenNotSelected() throws MalformedURLException, RemoteException {
        GTask task = new GTask();
        task.setSummary("something");
        task.setPriority(700);
        GTaskToJira converter = createConverterWithUnselectedField(GTaskDescriptor.FIELD.PRIORITY);
        IssueInput issue = converter.convertToJiraIssue(task);
        assertNull(issue.getField(IssueFieldId.PRIORITY_FIELD.id));
    }

    @Test
    public void issueTypeExported() throws MalformedURLException, RemoteException, URISyntaxException {
        GTask task = new GTask();
        task.setSummary("checking issueType");

        GTaskToJira converter = getConverter();
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
        GTaskToJira converter = getConverter();

        IssueInput issue = converter.convertToJiraIssue(task);
        // must be default issue type if we set the field to null
        assertEquals(findDefaultIssueTypeId(), getId(issue, IssueFieldId.ISSUE_TYPE_FIELD.id));
    }

    @Test
    public void summaryIsConvertedByDefault() {
        checkSummary(getConverter(), "summary here");
    }

    @Test
    public void summaryIsNotConvertedWhenNotSelected() {
        checkSummary(createConverterWithUnselectedField(GTaskDescriptor.FIELD.SUMMARY), null);
    }

    @Test
    public void summaryIsConvertedWhenSelected() {
        checkSummary(createConverterWithSelectedField(GTaskDescriptor.FIELD.SUMMARY), "summary here");
    }

    private void checkSummary(GTaskToJira converter, String expectedValue) {
        GTask task = new GTask();
        String summary = "summary here";
        task.setSummary(summary);
        IssueInput issueInput = converter.convertToJiraIssue(task);
        assertEquals(expectedValue, getValue(issueInput, IssueFieldId.SUMMARY_FIELD.id));
    }

    @Test
    public void descriptionIsConvertedByDefault() {
        checkDescription(getConverter(), "description here");
    }

    @Test
    public void descriptionIsConvertedWhenSelected() {
        checkDescription(createConverterWithSelectedField(GTaskDescriptor.FIELD.DESCRIPTION), "description here");
    }

    @Test
    public void descriptionIsNOTConvertedWhenNotSelected() {
        checkDescription(createConverterWithUnselectedField(GTaskDescriptor.FIELD.DESCRIPTION), null);
    }

    private void checkDescription(GTaskToJira converter, String expectedValue) {
        GTask task = new GTask();
        task.setDescription("description here");
        IssueInput issueInput = converter.convertToJiraIssue(task);
        assertEquals(expectedValue, getValue(issueInput, IssueFieldId.DESCRIPTION_FIELD.id));
    }

    @Test
    public void dueDateConvertedByDefault() {
        checkDueDate(getConverter(), "2014-04-28");
    }

    @Test
    public void dueDateConvertedWhenSelected() {
        checkDueDate(createConverterWithSelectedField(GTaskDescriptor.FIELD.DUE_DATE), "2014-04-28");
    }

    @Test
    public void dueDateNotConvertedWhenNotSelected() {
        checkDueDate(createConverterWithUnselectedField(GTaskDescriptor.FIELD.DUE_DATE), null);
    }

    private void checkDueDate(GTaskToJira converter, String expected) {
        GTask task = new GTask();
        Calendar calendar = Calendar.getInstance();
        calendar.set(2014, 3, 28, 0, 0, 0);
        task.setDueDate(calendar.getTime());
        IssueInput issueInput = converter.convertToJiraIssue(task);
        assertEquals(expected, getValue(issueInput, IssueFieldId.DUE_DATE_FIELD.id));
    }

    @Test
    public void assigneeConvertedByDefault() {
        checkAssignee(getConverter(), "mylogin");
    }

    @Test
    public void assigneeNotConvertedWhenNotSelected() {
        checkAssignee(createConverterWithUnselectedField(GTaskDescriptor.FIELD.ASSIGNEE), null);
    }

    @Test
    public void assigneeConvertedIfSelected() {
        checkAssignee(createConverterWithSelectedField(GTaskDescriptor.FIELD.ASSIGNEE), "mylogin");
    }

    private void checkAssignee(GTaskToJira converter, String expected) {
        GTask task = new GTask();
        task.setAssignee(new GUser("mylogin"));
        IssueInput issue = converter.convertToJiraIssue(task);
        assertEquals(expected, getComplexValue(issue, IssueFieldId.ASSIGNEE_FIELD.id, "name"));
    }

    @Test
    public void estimatedTimeConvertedByDefault() {
        checkEstimatedTime(getConverter(), "180m");
    }

    @Test
    public void estimatedTimeConvertedIfSelected() {
        checkEstimatedTime(createConverterWithSelectedField(GTaskDescriptor.FIELD.ESTIMATED_TIME), "180m");
    }

    @Test
    public void estimatedTimeNotConvertedIfNotSelected() {
        checkEstimatedTime(createConverterWithUnselectedField(GTaskDescriptor.FIELD.ESTIMATED_TIME), null);
    }

    private void checkEstimatedTime(GTaskToJira converter, String expectedTime) {
        GTask task = new GTask();
        task.setEstimatedHours(3f);
        IssueInput issue = converter.convertToJiraIssue(task);
        assertEquals(expectedTime, getComplexValue(issue, "timetracking", "originalEstimate"));
    }

    private String getId(IssueInput issue, String fieldName) {
        FieldInput field = issue.getField(fieldName);
        ComplexIssueInputFieldValue value = (ComplexIssueInputFieldValue) field.getValue();
        return (String) value.getValuesMap().get("id");
    }

    private String getValue(IssueInput issue, String fieldName) {
        FieldInput field = issue.getField(fieldName);
        if (field == null) {
            return null;
        }
        return (String) field.getValue();
    }

    private String getComplexValue(IssueInput issue, String fieldName, String subFieldName) {
        FieldInput field = issue.getField(fieldName);
        if (field == null) {
            return null;
        }
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

    private GTaskToJira getConverter() {
        GTaskToJira converter = new GTaskToJira(config);
        converter.setPriorities(priorities);
        converter.setIssueTypeList(issueTypeList);
        converter.setVersions(versions);
        converter.setComponents(components);
        return converter;
    }

    private GTaskToJira createConverterWithSelectedField(GTaskDescriptor.FIELD field) {
        return createConverterWithField(field, true);
    }

    private GTaskToJira createConverterWithUnselectedField(GTaskDescriptor.FIELD field) {
        return createConverterWithField(field, false);
    }

    private GTaskToJira createConverterWithField(GTaskDescriptor.FIELD field, boolean selected) {
        JiraConfig config = new JiraTestData().createTestConfig();
        config.getFieldMappings().setMapping(field, selected, null);
        GTaskToJira converter = new GTaskToJira(config);
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
