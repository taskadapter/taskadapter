package com.taskadapter.connector.github;

import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.service.UserService;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class GTaskToGithubTest {

    @Test
    public void emptyAssigneeIsIgnored() throws ConnectorException {
        GTask task = new GTask();
        task.setValue(GithubField.summary(), "my");
        task.setValue(GithubField.assignee(), null);
        Issue issue = getConverter().toIssue(task);
        assertEquals("my", issue.getTitle());
    }

    @Test
    public void assigneeWithNullLoginNameIsIgnored() throws ConnectorException {
        GTask task = new GTask();
        task.setValue(GithubField.summary(), "my");
        task.setValue(GithubField.assignee(), null);
        Issue issue = getConverter().toIssue(task);
        assertEquals("my", issue.getTitle());
    }

    @Test
    public void summaryIsConvertedByDefault() throws ConnectorException {
        Issue issue = getConverter().toIssue(createTask("summary1"));
        assertEquals("summary1", issue.getTitle());
    }
/*

    @Test
    public void summaryIsNOTConvertedInUnmapped() throws ConnectorException {
        Collection<GTaskDescriptor.FIELD> selectedFields = FieldSelector.getSelectedFields(GithubSupportedFields.SUPPORTED_FIELDS,
                GTaskDescriptor.FIELD.SUMMARY, false);
        Issue issue = getConverter(selectedFields).toIssue(createTask("summary1"));
        assertNull(issue.getTitle());
    }
*/

    @Test
    public void descriptionIsConvertedByDefault() throws ConnectorException {
        Issue issue = getConverter().toIssue(createTask("summary1", "descr1"));
        assertEquals("descr1", issue.getBody());
    }
/*

    @Test
    public void descriptionIsNOTConvertedInUnmapped() throws ConnectorException {
        Collection<GTaskDescriptor.FIELD> selectedFields = FieldSelector.getSelectedFields(GithubSupportedFields.SUPPORTED_FIELDS,
                GTaskDescriptor.FIELD.DESCRIPTION, false);
        Issue issue = getConverter(selectedFields).toIssue(createTask("summary1", "description 1"));
        assertNull(issue.getBody());
    }
*/

    private GTaskToGithub getConverter() {
        UserService mock = mock(UserService.class);
        return new GTaskToGithub(mock);
    }

    private GTask createTask(String summary) {
        return createTask(summary, null);
    }

    private GTask createTask(String summary, String description) {
        GTask task = new GTask();
        task.setValue(GithubField.summary(), summary);
        task.setValue(GithubField.description(), description);
        return task;
    }
}
