package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.connector.definition.exception.FieldConversionException;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskBuilder;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JiraTaskTypeResolverTest {
    private final static TaskId someId = new TaskId(1L, "KEY1");
    private final static String topLevel = "default parent";
    private final static String sub = "default sub";
    private final static GTaskToJira converter = GTaskToJiraFactory.getConverter();

    @Test
    public void resolvesDefaultTaskType() throws FieldConversionException {
        verify(GTaskBuilder.withSummary(), topLevel);
    }

    @Test
    public void resolvesDefaultSubTaskType() throws FieldConversionException {
        verify(GTaskBuilder.withSummary().setParentIdentity(someId), sub);
    }

    @Test
    public void keepsExplicitType() throws FieldConversionException {
        verify(new GTask().setValue(AllFields.taskType, "Task"), "Task");
    }

    @Test
    public void keepsExplicitTypeEvenIfParentIsSet() throws FieldConversionException {
        verify(new GTask().setValue(AllFields.taskType, "Task").setParentIdentity(someId), "Task");
    }

    private static void verify(GTask task, String expected) throws FieldConversionException {
        assertThat(JiraTaskTypeResolver.resolveIssueTypeNameForCreate(converter.convertToJiraIssue(task),
                topLevel, sub)).isEqualTo(expected);
    }

}
