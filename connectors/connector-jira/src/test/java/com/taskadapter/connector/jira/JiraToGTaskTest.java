package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.domain.Issue;
import com.taskadapter.connector.Priorities;
import com.taskadapter.model.GTask;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class JiraToGTaskTest {
    private static Priorities priorities;

    @BeforeClass
    public static void oneTimeSetUp() {
        try {
            priorities = JiraConfig.createDefaultPriorities();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Can't init JIRA tests: " + e.toString());
        }
    }

    @Test
    public void summaryIsConverted() throws Exception {
        Issue issue = MockData.loadIssue("issue_jira_5.0.1.json");
        GTask task = convertIssue(issue);
        assertEquals(issue.getSummary(), task.getSummary());
    }

    @Test
    public void descriptionIsConverted() throws Exception {
        Issue issue = MockData.loadIssue("issue_jira_5.0.1.json");
        GTask task = convertIssue(issue);
        assertEquals(issue.getDescription(), task.getDescription());
    }

    @Test
    public void estimatedTimeConvertedByDefault() throws Exception {
        Issue issue = MockData.loadIssue("issue_with_time_tracking_5.0.json");
        GTask task = convertIssue(issue);
        assertEquals((Float) 45.5f, task.getEstimatedHours());
    }

    @Test
    public void assigneeIsConverted() throws Exception {
        Issue issue = MockData.loadIssue("issue_with_assignee_5.0.json");
        GTask task = convertIssue(issue);
        assertEquals(issue.getAssignee().getName(), task.getAssignee().getLoginName());
        assertEquals(issue.getAssignee().getDisplayName(), task.getAssignee().getDisplayName());
    }

    @Test
    public void issueTypeIsConverted() throws Exception {
        Issue issue = MockData.loadIssue("issue_jira_5.0.1.json");
        GTask task = convertIssue(issue);
        assertEquals(issue.getIssueType().getName(), task.getType());
    }

    @Test
    public void dueDateNullValueIsConverted() throws Exception {
        Issue issue = MockData.loadIssue("issue_jira_5.0.1.json");
        GTask task = convertIssue(issue);
        assertNull(task.getDueDate());
    }

    @Test
    public void dueDateIsConverted() throws Exception {
        Issue issue = MockData.loadIssue("issue_jira_duedate_5.0.1.json");
        GTask task = convertIssue(issue);
        assertEquals(issue.getDueDate().toDate().getTime(), task.getDueDate().getTime());
    }

    @Test
    public void setDefaultPriorityWhenIsNull() throws Exception {
        Issue issue = MockData.loadIssue("issue_jira_5.0.1.json");
        GTask task = convertIssue(issue);
        assertEquals(Priorities.DEFAULT_PRIORITY_VALUE, task.getPriority());
    }

    private GTask convertIssue(final Issue issue) {
        JiraToGTask jiraToGTask = new JiraToGTask(priorities);
        return jiraToGTask.convertToGenericTask(issue);
    }
}
