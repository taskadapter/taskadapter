package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.domain.Issue;
import com.taskadapter.connector.definition.PriorityResolver;
import com.taskadapter.model.GTask;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JiraToGTaskTest {

    @Test
    public void summaryIsConverted() throws Exception {
        Issue issue = MockData.loadIssue("issue_jira_5.0.1.json");
        JiraToGTask jiraToGTask = new JiraToGTask(getPriorityResolver());
        GTask task = jiraToGTask.convertToGenericTask(issue);
        assertEquals(issue.getSummary(), task.getSummary());
    }

    @Test
    public void descriptionIsConverted() throws Exception {
        Issue issue = MockData.loadIssue("issue_jira_5.0.1.json");
        JiraToGTask jiraToGTask = new JiraToGTask(getPriorityResolver());
        GTask task = jiraToGTask.convertToGenericTask(issue);
        assertEquals(issue.getDescription(), task.getDescription());
    }

    @Test
    public void estimatedTimeConvertedByDefault() throws Exception {
        Issue issue = MockData.loadIssue("issue_with_time_tracking_5.0.json");
        JiraToGTask jiraToGTask = new JiraToGTask(getPriorityResolver());
        GTask task = jiraToGTask.convertToGenericTask(issue);
        assertEquals((Float) 45.5f, task.getEstimatedHours());
    }

    @Test
    public void assigneeIsConverted() throws Exception {
        Issue issue = MockData.loadIssue("issue_with_assignee_5.0.json");
        JiraToGTask jiraToGTask = new JiraToGTask(getPriorityResolver());
        GTask task = jiraToGTask.convertToGenericTask(issue);
        assertEquals(issue.getAssignee().getName(), task.getAssignee().getLoginName());
        assertEquals(issue.getAssignee().getDisplayName(), task.getAssignee().getDisplayName());
    }

    @Test
    public void issueTypeIsConverted() throws Exception {
        Issue issue = MockData.loadIssue("issue_with_assignee_5.0.json");
        JiraToGTask jiraToGTask = new JiraToGTask(getPriorityResolver());
        GTask task = jiraToGTask.convertToGenericTask(issue);
        assertEquals(issue.getIssueType().getName(), task.getType());
    }

    private GTask loadAndConvertIssue() throws Exception {
        Issue issue = MockData.loadIssue("issue_with_assignee_5.0.json");
        JiraToGTask jiraToGTask = new JiraToGTask(getPriorityResolver());
        return jiraToGTask.convertToGenericTask(issue);
    }
    
    private PriorityResolver getPriorityResolver() {
        return new PriorityResolver() {
            @Override
            public Integer getPriorityNumberByName(String priorityName) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        };
    }
}
