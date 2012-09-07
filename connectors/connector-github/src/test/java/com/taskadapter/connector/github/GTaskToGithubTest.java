package com.taskadapter.connector.github;

import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GUser;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.service.UserService;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class GTaskToGithubTest {

    @Test
    public void emptyAssigneeIsIgnored() throws ConnectorException {
        UserService mock = mock(UserService.class);
        GTaskToGithub gTaskToGithub = new GTaskToGithub(mock);
        GTask task = new GTask();
        task.setSummary("my");
        task.setAssignee(null);
        Issue issue = gTaskToGithub.toIssue(task);
        assertEquals("my", issue.getTitle());
    }

    @Test
    public void assigneeWithNullLoginNameIsIgnored() throws ConnectorException {
        UserService mock = mock(UserService.class);
        GTaskToGithub gTaskToGithub = new GTaskToGithub(mock);
        GTask task = new GTask();
        task.setSummary("my");
        GUser assignee = new GUser();
        task.setAssignee(assignee);
        Issue issue = gTaskToGithub.toIssue(task);
        assertEquals("my", issue.getTitle());
    }

}
