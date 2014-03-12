package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.TaskSaveResult;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.testlib.CommonTests;
import com.taskadapter.connector.testlib.TestMappingUtils;
import com.taskadapter.connector.testlib.TestUtils;
import com.taskadapter.model.GTask;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class JiraConnectorTest {
    private static final Mappings TEST_MAPPINGS = TestMappingUtils.fromFields(JiraSupportedFields.SUPPORTED_FIELDS);

    @Test
    public void testLoadTaskByKey() throws ConnectorException {
        JiraConnector connector = getConnector();
        GTask task = new GTask();
        String summary = "load by key";
        task.setSummary(summary);
        task.setType("Task");
        String key = TestUtils.save(connector, task, TEST_MAPPINGS);
        GTask loadedTask = connector.loadTaskByKey(key, TEST_MAPPINGS);
        assertThat(loadedTask.getSummary()).isEqualTo(summary);
    }

    @Test
    public void descriptionSavedByDefault() throws ConnectorException {
        new CommonTests().descriptionSavedByDefault(getConnector(), TEST_MAPPINGS);
    }

    @Test
    public void subtasksAreCreated() throws ConnectorException {
        GTask parentTask = TestUtils.generateTask();
        parentTask.setId(123123);
        parentTask.setSummary("parent task");
        List<GTask> subTasks = TestUtils.generateTasks(2);
        parentTask.getChildren().addAll(subTasks);
        JiraConnector connector = getConnector();
        TaskSaveResult result = connector.saveData(Arrays.asList(parentTask), null, TEST_MAPPINGS);
        assertThat(result.getCreatedTasksNumber()).isEqualTo(3);

        String remoteKey = result.getRemoteKey(123123);
        GTask loadedParentTask = connector.loadTaskByKey(remoteKey, TEST_MAPPINGS);

        // TODO this FAILS!
        // only the task itself is loaded, no subtasks (even though they were created successfully)
        assertThat(loadedParentTask.getChildren()).hasSize(2);
    }

    private JiraConnector getConnector() {
        return new JiraConnector(new JiraTestData().createTestConfig());
    }
}
