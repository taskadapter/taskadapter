package com.taskadapter.connector.jira;

import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.TaskSaveResult;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.testlib.CommonTests;
import com.taskadapter.connector.testlib.TestMappingUtils;
import com.taskadapter.connector.testlib.TestUtils;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
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

    @Ignore("This test requires a custom project configuration: project with 'ENV' key")
    @Test
    public void taskIsCreatedInProjectWithRequiredEnvironmentField() throws ConnectorException {
        GTask task = TestUtils.generateTask();
        task.setType("Bug");
        String environmentString = "some environment";
        task.setEnvironment(environmentString);
        JiraConfig testConfig = new JiraTestData().createTestConfig();
        // special project with Environment set as a required field
        testConfig.setProjectKey("ENV");
        JiraConnector connector = new JiraConnector(testConfig);
        TaskSaveResult result = connector.saveData(Arrays.asList(task), null, TEST_MAPPINGS);
        assertThat(result.getCreatedTasksNumber()).isEqualTo(1);
        // TODO this is ugly
        Collection<String> values = result.getIdToRemoteKeyMap().values();
        String key = values.iterator().next();
        GTask loadedTask = connector.loadTaskByKey(key,  new Mappings());
        assertThat(loadedTask.getEnvironment()).isEqualTo(environmentString);
    }

    // TODO move to some generic tests, this is not Jira-specific
    @Test
    public void taskIsCreatedWithDefaultEnvironmentField() throws ConnectorException {
        GTask task = TestUtils.generateTask();
        String environmentString = "some environment";
        JiraConfig testConfig = new JiraTestData().createTestConfig();
        JiraConnector connector = new JiraConnector(testConfig);

        Mappings mappings = TestMappingUtils.fromFields(JiraSupportedFields.SUPPORTED_FIELDS);
        mappings.setMapping(GTaskDescriptor.FIELD.ENVIRONMENT, true,
                JiraSupportedFields.SUPPORTED_FIELDS.getDefaultValue(GTaskDescriptor.FIELD.ENVIRONMENT),
                environmentString);

        TaskSaveResult result = connector.saveData(Arrays.asList(task), null, mappings);
        assertThat(result.getCreatedTasksNumber()).isEqualTo(1);
        // TODO this is ugly
        Collection<String> values = result.getIdToRemoteKeyMap().values();
        String key = values.iterator().next();
        GTask loadedTask = connector.loadTaskByKey(key,  new Mappings());
        assertThat(loadedTask.getEnvironment()).isEqualTo(environmentString);
    }

    private JiraConnector getConnector() {
        return new JiraConnector(new JiraTestData().createTestConfig());
    }
}
