package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.api.domain.IssueFieldId;
import com.atlassian.jira.rest.client.api.domain.Priority;
import com.atlassian.jira.rest.client.api.domain.input.ComplexIssueInputFieldValue;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.taskadapter.connector.definition.exception.FieldConversionException;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskBuilder;
import org.junit.Test;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class GTaskToJiraTest {
    @Test
    public void priorityConvertedToCritical() throws FieldConversionException {
        var priorityCritical = find(GTaskToJiraFactory.defaultPriorities, "Highest");
        var task = JiraGTaskBuilder.builderWithSummary().withPriority(750).build();
        var converter = getConverter();
        var newIssue = converter.convertToJiraIssue(task).getIssueInput();
        var actualPriorityId = getId(newIssue, IssueFieldId.PRIORITY_FIELD.id);
        assertThat(actualPriorityId).isEqualTo(priorityCritical.getId() + "");
    }

    /**
     * regression test: priority conversion was failing with exception when priorities loaded from server did not contain
     * priority name set in JIRA config. e.g. if JIRA is set to have non-english priority names,
     * while TaskAdapter JIRA config has english names in its "priority mapping" table.
     */
    @Test
    public void unknownPriorityNameGivesUserFriendlyError() {
        var task = JiraGTaskBuilder.builderWithSummary().withPriority(500).build();
        var converter = GTaskToJiraFactory.getConverter(java.util.List.of());
        assertThatThrownBy(() -> converter.convertToJiraIssue(task).getIssueInput())
                .isInstanceOf(FieldConversionException.class)
                .hasMessageContaining("Reason: Priority with name Medium is not found on the server");
    }

    @Test
    public void summaryIsConverted() throws FieldConversionException {
        checkSummary(getConverter(), "summary here");
    }

    private void checkSummary(GTaskToJira converter, String expected) throws FieldConversionException {
        var task = GTaskBuilder.withSummary(expected);
        var issueInput = converter.convertToJiraIssue(task).getIssueInput();
        assertThat(getValue(issueInput, IssueFieldId.SUMMARY_FIELD.id))
                .isEqualTo(expected);
    }

    @Test
    public void description() throws FieldConversionException {
        checkDescription(getConverter(), "description here");
    }

    @Test
    public void status() throws FieldConversionException {
        var task = new GTask().setValue(AllFields.taskStatus, "TO DO");
        assertThat(getConverter().convertToJiraIssue(task).getStatus())
                .isEqualTo("TO DO");
    }

    private void checkDescription(GTaskToJira converter, String expected) throws FieldConversionException {
        var task = new GTask().setValue(AllFields.description, expected);

        var issueInput = converter.convertToJiraIssue(task).getIssueInput();
        assertThat(getValue(issueInput, IssueFieldId.DESCRIPTION_FIELD.id)).isEqualTo(expected);
    }

    @Test
    public void dueDateConvertedByDefault() throws FieldConversionException {
        checkDueDate(getConverter(), "2014-04-28");
    }

    private void checkDueDate(GTaskToJira converter, String expected) throws FieldConversionException {
        var task = new GTask();
        var calendar = Calendar.getInstance();
        calendar.set(2014, Calendar.APRIL, 28, 0, 0, 0);
        task.setValue(AllFields.dueDate, calendar.getTime());
        var issueInput = converter.convertToJiraIssue(task).getIssueInput();
        assertThat(getValue(issueInput, IssueFieldId.DUE_DATE_FIELD.id))
                .isEqualTo(expected);
    }

    @Test
    public void reporterLoginName() throws FieldConversionException {
        var task = new GTask().setValue(AllFields.reporterLoginName, "mylogin");
        var issue = getConverter().convertToJiraIssue(task).getIssueInput();
        assertThat(getComplexValue(issue, IssueFieldId.REPORTER_FIELD.id, "name"))
                .isEqualTo("mylogin");
    }

    @Test
    public void assigneeConvertedByDefault() throws FieldConversionException {
        checkAssignee(getConverter(), "mylogin");
    }

    private void checkAssignee(GTaskToJira converter, String expected) throws FieldConversionException {
        var task = new GTask().setValue(AllFields.assigneeLoginName, expected);
        var issue = converter.convertToJiraIssue(task).getIssueInput();
        assertThat(getComplexValue(issue, IssueFieldId.ASSIGNEE_FIELD.id, "name"))
                .isEqualTo(expected);
    }

    @Test
    public void componentsUseOnlyTheFirstProvidedValueAndIgnoreOthers() throws FieldConversionException {
        var task = new GTask().setValue(AllFields.components, java.util.List.of("client", "server"));
        var issue = getConverter().convertToJiraIssue(task).getIssueInput();
        assertThat(getIterableValue(issue, IssueFieldId.COMPONENTS_FIELD.id))
                .containsOnly("client");
    }

    @Test
    public void emptyComponentIsValid() throws FieldConversionException {
        var task = new GTask().setValue(AllFields.components, new java.util.ArrayList<String>());
        var issue = getConverter().convertToJiraIssue(task).getIssueInput();
        assertThat(getIterableValue(issue, IssueFieldId.COMPONENTS_FIELD.id)).isNull();
    }

    @Test
    public void estimatedTime() throws FieldConversionException {
        checkEstimatedTime(getConverter(), "180m");
    }

    private static void checkEstimatedTime(GTaskToJira converter, String expectedTime) throws FieldConversionException {
        var task = new GTask();
        task.setValue(AllFields.estimatedTime, 3F);
        var issue = converter.convertToJiraIssue(task).getIssueInput();
        assertThat(getComplexValue(issue, "timetracking", "originalEstimate"))
                .isEqualTo(expectedTime);
    }

    private String getId(IssueInput issue, String fieldName) {
        var field = issue.getField(fieldName);
        var value = (ComplexIssueInputFieldValue) field.getValue();
        return (String) value.getValuesMap().get("id");
    }

    private static String getValue(IssueInput issue, String fieldName) {
        var field = issue.getField(fieldName);
        if (field == null) {
            return null;
        }
        return (String) field.getValue();
    }

    private static String getComplexValue(IssueInput issue, String fieldName, String subFieldName) {
        var field = issue.getField(fieldName);
        if (field == null) {
            return null;
        }
        var value = (ComplexIssueInputFieldValue) field.getValue();
        return (String) value.getValuesMap().get(subFieldName);
    }

    private List<String> getIterableValue(IssueInput issue, String fieldName) {
        var field = issue.getField(fieldName);
        if (field == null) {
            return null;
        }
        var value = (Iterable<ComplexIssueInputFieldValue>) field.getValue();
        return StreamSupport.stream(value.spliterator(), false)
                .map(v -> (String) v.getValuesMap().get("name"))
                .collect(Collectors.toList());
    }

    private static GTaskToJira getConverter() {
        return GTaskToJiraFactory.getConverter();
    }

    private static Priority find(Iterable<Priority> priorities, String priorityName) {
        return StreamSupport.stream(priorities.spliterator(), false)
                .filter(priority -> priority.getName().equals(priorityName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Priority not found: " + priorityName));
    }
}
