package com.taskadapter.connector.github;

import com.taskadapter.connector.definition.exception.FieldConversionException;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskBuilder;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GTaskToGithubTest {
    @Test
    public void emptyAssigneeIsIgnored() throws FieldConversionException {
        var task = new GTask().setValue(AllFields.summary, "my").setValue(AllFields.assigneeLoginName, null);
        var issue = getConverter().toIssue(task);
        assertThat(issue.getTitle()).isEqualTo("my");
    }

    @Test
    public void assigneeWithNullLoginNameIsIgnored() throws FieldConversionException {
        var task = new GTask();
        task.setValue(AllFields.summary, "my");
        task.setValue(AllFields.assigneeLoginName, null);
        var issue = getConverter().toIssue(task);
        assertThat(issue.getTitle()).isEqualTo("my");
    }

    @Test
    public void assigneeIsSet() throws FieldConversionException {
        var task = new GTaskBuilder().withAssigneeLogin("login").build();
        var issue = getConverter().toIssue(task);
        assertThat(issue.getAssignee().getLogin()).isEqualTo("login");

    }

    @Test
    public void summaryIsConvertedByDefault() throws FieldConversionException {
        var issue = getConverter().toIssue(createTask("summary1"));
        assertThat(issue.getTitle()).isEqualTo("summary1");

    }

    @Test
    public void descriptionIsConvertedByDefault() throws FieldConversionException {
        var issue = getConverter().toIssue(createTask("summary1", "descr1"));
        assertThat(issue.getBody()).isEqualTo("descr1");
    }

    private static GTaskToGithub getConverter() {
        return new GTaskToGithub(new MockUserService());
    }

    private static GTask createTask(String summary) {
        return createTask(summary, null);
    }

    private static GTask createTask(String summary, String description) {
        return new GTask().setValue(AllFields.summary, summary).setValue(AllFields.description, description);
    }
}
