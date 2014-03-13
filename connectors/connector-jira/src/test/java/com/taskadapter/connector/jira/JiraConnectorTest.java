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
        parentTask.setId(11);
        parentTask.setSummary("parent task");
        List<GTask> subTasks = TestUtils.generateTasks(2);
        subTasks.get(0).setId(22);
        subTasks.get(1).setId(33);
        parentTask.getChildren().addAll(subTasks);
        JiraConnector connector = getConnector();
        TaskSaveResult result = connector.saveData(Arrays.asList(parentTask), null, TEST_MAPPINGS);
        assertThat(result.getCreatedTasksNumber()).isEqualTo(3);

        String remoteKey = result.getRemoteKey(11);
        String subTask1RemoteKey = result.getRemoteKey(22);
        String subTask2RemoteKey = result.getRemoteKey(33);
        GTask loadedSubTask1 = connector.loadTaskByKey(subTask1RemoteKey, TEST_MAPPINGS);
        GTask loadedSubTask2 = connector.loadTaskByKey(subTask2RemoteKey, TEST_MAPPINGS);

        assertThat(loadedSubTask1.getParentKey()).isEqualTo(remoteKey);
        assertThat(loadedSubTask2.getParentKey()).isEqualTo(remoteKey);

        // TODO need to delete the temporary tasks
    }

    private JiraConnector getConnector() {
        return new JiraConnector(new JiraTestData().createTestConfig());
    }
}
