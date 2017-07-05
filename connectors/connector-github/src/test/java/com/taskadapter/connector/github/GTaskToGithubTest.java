package com.taskadapter.connector.github;

import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.testlib.FieldSelector;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor;
import com.taskadapter.model.GUser;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.service.UserService;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

public class GTaskToGithubTest {

    @Test
    public void emptyAssigneeIsIgnored() throws ConnectorException {
        GTask task = new GTask();
        task.setSummary("my");
        task.setAssignee(null);
        Issue issue = getConverter().toIssue(task);
        assertEquals("my", issue.getTitle());
    }

    @Test
    public void assigneeWithNullLoginNameIsIgnored() throws ConnectorException {
        GTask task = new GTask();
        task.setSummary("my");
        GUser assignee = new GUser();
        task.setAssignee(assignee);
        Issue issue = getConverter().toIssue(task);
        assertEquals("my", issue.getTitle());
    }

    @Test
    public void summaryIsConvertedByDefault() throws ConnectorException {
        Issue issue = getConverter().toIssue(createTask("summary1"));
        assertEquals("summary1", issue.getTitle());
    }

    @Test
    public void summaryIsNOTConvertedInUnmapped() throws ConnectorException {
        Collection<GTaskDescriptor.FIELD> selectedFields = FieldSelector.getSelectedFields(GithubSupportedFields.SUPPORTED_FIELDS,
                GTaskDescriptor.FIELD.SUMMARY, false);
        Issue issue = getConverter(selectedFields).toIssue(createTask("summary1"));
        assertNull(issue.getTitle());
    }

    @Test
    public void descriptionIsConvertedByDefault() throws ConnectorException {
        Issue issue = getConverter().toIssue(createTask("summary1", "descr1"));
        assertEquals("descr1", issue.getBody());
    }

    @Test
    public void descriptionIsNOTConvertedInUnmapped() throws ConnectorException {
        Collection<GTaskDescriptor.FIELD> selectedFields = FieldSelector.getSelectedFields(GithubSupportedFields.SUPPORTED_FIELDS,
                GTaskDescriptor.FIELD.DESCRIPTION, false);
        Issue issue = getConverter(selectedFields).toIssue(createTask("summary1", "description 1"));
        assertNull(issue.getBody());
    }

    private GTaskToGithub getConverter() {
        return getConverter(GithubSupportedFields.SUPPORTED_FIELDS.getSupportedFields());
    }

    private GTaskToGithub getConverter(Collection<GTaskDescriptor.FIELD> fieldsToExport) {
        UserService mock = mock(UserService.class);
        return new GTaskToGithub(mock, fieldsToExport);
    }

    private GTask createTask(String summary) {
        return createTask(summary, null);
    }

    private GTask createTask(String summary, String description) {
        GTask task = new GTask();
        task.setSummary(summary);
        task.setDescription(description);
        return task;
    }
}
