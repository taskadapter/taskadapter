package com.taskadapter.connector.jira;

import com.atlassian.jira.rpc.soap.client.RemoteIssue;
import com.taskadapter.model.GTask;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;

public class JiraTaskConverterTest {

    @Test
    public void convertToGenericTask() {
        JiraConfig config = new JiraConfig();
        JiraTaskConverter taskConverter = new JiraTaskConverter(config);

        String id = "123";
        String key = "key";
        String assignee = "assignee";
        String summary = "some summary for task";
        String description = "description";
        Calendar dueDate = Calendar.getInstance();
        dueDate.set(2012, 11, 23);

        RemoteIssue issue = new RemoteIssue();
        issue.setId(id);
        issue.setKey(key);
        issue.setSummary(summary);
        issue.setDescription(description);
        issue.setDuedate(dueDate);
        issue.setAssignee(assignee);

        GTask task = taskConverter.convertToGenericTask(issue);

        assertEquals(task.getId().intValue(), Integer.parseInt(id));
        assertEquals(task.getKey(), key);
        assertEquals(task.getAssignee().getLoginName(), assignee);
        assertEquals(task.getSummary(), summary);
        assertEquals(task.getDescription(), description);
        assertEquals(task.getDueDate(), dueDate.getTime());
    }
}
