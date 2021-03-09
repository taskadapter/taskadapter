package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.taskadapter.connector.Priorities;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.CustomListString;
import com.taskadapter.model.GTask;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JiraToGTaskTest {
    private static Priorities priorities = JiraConfig.createDefaultPriorities();

    @Test
    public void summaryIsConverted() {
        var issue = MockData.loadIssue("issue_jira_5.0.1.json");
        var task = convertIssue(issue);
        assertThat(task.getValue(AllFields.summary)).isEqualTo(issue.getSummary());
    }

    @Test
    public void descriptionIsConverted() {
        var issue = MockData.loadIssue("issue_jira_5.0.1.json");
        var task = convertIssue(issue);
        assertThat(task.getValue(AllFields.description)).isEqualTo(issue.getDescription());
    }

    @Test
    public void status() {
        var issue = MockData.loadIssue("issue_jira_5.0.1.json");
        assertThat(convertIssue(issue).getValue(AllFields.taskStatus)).isEqualTo(issue.getStatus().getName());
    }

    @Test
    public void estimatedTimeConvertedByDefault() {
        var issue = MockData.loadIssue("issue_with_time_tracking_5.0.json");
        var task = convertIssue(issue);
        assertThat(task.getValue(AllFields.estimatedTime)).isEqualTo(45.5f);
    }

    @Test
    public void assigneeIsConverted() { // TODO cannot parse an issue without "names" and "schema" section. submitted a bug:
        // https://answers.atlassian.com/questions/32971227/jira-java-rest-client-cannot-parse-a-valid-issue-json-returned-by-jira-6.4.11-npe-at-jsonparseutil.getstringkeysjsonparseutil.java337
        var issue = MockData.loadIssue("issue_with_assignee_6.4.11_expanded_names_and_schema.json");
        var task = convertIssue(issue);
        assertThat(task.getValue(AllFields.assigneeLoginName)).isEqualTo(issue.getAssignee().getName());
    }

    @Test
    public void reporter() { // TODO cannot parse an issue without "names" and "schema" section. submitted a bug:
        // https://answers.atlassian.com/questions/32971227/jira-java-rest-client-cannot-parse-a-valid-issue-json-returned-by-jira-6.4.11-npe-at-jsonparseutil.getstringkeysjsonparseutil.java337
        var issue = MockData.loadIssue("issue_with_assignee_6.4.11_expanded_names_and_schema.json");
        assertThat(convertIssue(issue).getValue(AllFields.reporterLoginName))
                .isEqualTo(issue.getReporter().getName());
    }

    @Test
    public void issueType() {
        var issue = MockData.loadIssue("issue_jira_5.0.1.json");
        var task = convertIssue(issue);
        assertThat(task.getValue(AllFields.taskType))
                .isEqualTo(issue.getIssueType().getName());
    }

    @Test
    public void dueDateNullValueIsConverted() {
        var issue = MockData.loadIssue("issue_jira_5.0.1.json");
        var task = convertIssue(issue);
        assertThat(task.getValue(AllFields.dueDate)).isNull();
    }

    @Test
    public void dueDateIsConverted() {
        var issue = MockData.loadIssue("issue_jira_duedate_5.0.1.json");
        var task = convertIssue(issue);
        assertThat(task.getValue(AllFields.dueDate))
                .isEqualTo(issue.getDueDate().toDate());
    }

    @Test
    public void createdOn() {
        var issue = MockData.loadIssue("issue_jira_duedate_5.0.1.json");
        var task = convertIssue(issue);
        assertThat(task.getValue(AllFields.createdOn))
                .isEqualTo(issue.getCreationDate().toDate());
    }

    @Test
    public void noComponentsGivesEmptyList() {
        var issue = MockData.loadIssue("issue_jira_duedate_5.0.1.json");
        assertThat(convertIssue(issue).getValue(AllFields.components))
                .isEqualTo(new ArrayList<String>());
    }

    @Test
    public void components() {
        var issue = MockData.loadIssue("issue_with_components_jira_7.1.9.json");
        assertThat(convertIssue(issue).getValue(AllFields.components))
                .isEqualTo(java.util.List.of("compiler", "gui"));
    }

    @Test
    public void setDefaultPriorityWhenIsNull() {
        var issue = MockData.loadIssue("issue_jira_5.0.1.json");
        var task = convertIssue(issue);
        assertThat(task.getValue(AllFields.priority))
                .isEqualTo(Priorities.DEFAULT_PRIORITY_VALUE);
    }

    @Test
    public void customFields() {
        var issue = MockData.loadIssue("7.1.9/issue_with_custom_options_checkboxes_jira_7.1.9.json");
        var jiraToGTask = new JiraToGTask(priorities);
        var fields = MockData.loadFieldDefinitions();
        var task = jiraToGTask.convertToGenericTask(new CustomFieldResolver(fields), issue);
        List<String> list = task.getValue(new CustomListString("custom_checkbox_1"));
        assertThat(list)
                .containsOnly("option1", "option2");
    }

    private static GTask convertIssue(Issue issue) {
        var jiraToGTask = new JiraToGTask(priorities);
        return jiraToGTask.convertToGenericTask(new CustomFieldResolver(List.of()), issue);
    }
}
