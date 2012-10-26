package com.taskadapter.connector.github;

import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.testlib.TestMappingUtils;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor;
import com.taskadapter.model.GUser;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.service.UserService;
import org.junit.Test;

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
        Mappings mappings = TestMappingUtils
        .fromFields(GithubSupportedFields.SUPPORTED_FIELDS);
        mappings.deselectField(GTaskDescriptor.FIELD.SUMMARY);
        Issue issue = getConverter(mappings).toIssue(createTask("summary1"));
        assertNull(issue.getTitle());
    }

    @Test
    public void descriptionIsConvertedByDefault() throws ConnectorException {
        Issue issue = getConverter().toIssue(createTask("summary1", "descr1"));
        assertEquals("descr1", issue.getBody());
    }

    @Test
    public void descriptionIsNOTConvertedInUnmapped() throws ConnectorException {
        Mappings mappings = TestMappingUtils
        .fromFields(GithubSupportedFields.SUPPORTED_FIELDS);
        mappings.deselectField(GTaskDescriptor.FIELD.DESCRIPTION);
        Issue issue = getConverter(mappings).toIssue(createTask("summary1", "description 1"));
        assertNull(issue.getBody());
    }

    private GTaskToGithub getConverter() {
        return getConverter(TestMappingUtils
        .fromFields(GithubSupportedFields.SUPPORTED_FIELDS));
    }

    private GTaskToGithub getConverter(Mappings mappings) {
        UserService mock = mock(UserService.class);
        return new GTaskToGithub(mock, mappings);
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
