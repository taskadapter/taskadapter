package com.taskadapter.connector.testlib;

import com.taskadapter.connector.common.ConnectorUtils;
import com.taskadapter.connector.definition.AvailableFields;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.TaskSaveResult;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor.FIELD;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public final class CommonTests {

    public static void testLoadTasks(Connector<?> connector, Mappings mappings) throws ConnectorException {
        int tasksQty = 1;
        List<GTask> tasks = TestUtils.generateTasks(tasksQty);

        String expectedSummaryTask1 = tasks.get(0).getSummary();
        Integer expectedID = tasks.get(0).getId();

        TaskSaveResult result = connector.saveData(tasks, null, mappings);
        assertEquals(tasksQty, result.getCreatedTasksNumber());

        Integer createdTask1Id = Integer.valueOf(result.getIdToRemoteKeyMap().get(expectedID));

        List<GTask> loadedTasks = ConnectorUtils.loadDataOrderedById(connector, mappings);
        // there could be some other previously created tasks
        assertTrue(loadedTasks.size() >= tasksQty);

        GTask foundTask = TestUtils.findTaskInList(loadedTasks, createdTask1Id);
        assertNotNull(foundTask);
        assertEquals(expectedSummaryTask1, foundTask.getSummary());
    }

    public static void descriptionSavedByDefault(Connector<?> connector, Mappings mappings) throws ConnectorException {
        GTask task = TestUtils.generateTask();
        GTask loadedTask = TestUtils.saveAndLoadViaSummary(connector, task, mappings);
        assertEquals(task.getDescription(), loadedTask.getDescription());
    }

    public static void descriptionSavedIfSelected(Connector<?> connector, Mappings mappings) throws ConnectorException {
        GTask task = TestUtils.generateTask();
        Mappings clonedMappings = new Mappings(mappings);
        GTask loadedTask = new TestSaver(connector, clonedMappings).selectField(FIELD.DESCRIPTION).saveAndLoad(task);
        assertEquals(task.getDescription(), loadedTask.getDescription());
    }

    public static void testCreates2Tasks(Connector<?> connector, Mappings mappings) throws ConnectorException {
        int tasksQty = 2;
        List<GTask> tasks = TestUtils.generateTasks(tasksQty);
        TaskSaveResult result = connector.saveData(tasks, null, mappings);
        assertFalse(result.hasErrors());
        assertEquals(tasksQty, result.getCreatedTasksNumber());
    }

    public static void taskCreatedAndUpdatedOK(Connector<?> connector, AvailableFields availableFields) throws ConnectorException {
        int tasksQty = 1;
        GTask task = TestUtils.generateTask();

        Integer id = task.getId();

        // CREATE
        final Mappings mappings = TestMappingUtils.fromFields(availableFields);
        TaskSaveResult result = connector.saveData(Arrays.asList(task), null, mappings);
        assertFalse(result.hasErrors());
        assertEquals(tasksQty, result.getCreatedTasksNumber());
        String remoteKey = result.getRemoteKey(id);

        GTask loaded = connector.loadTaskByKey(remoteKey, mappings);

        // UPDATE
        String NEW_SUMMARY = "new summary here";
        loaded.setSummary(NEW_SUMMARY);
        loaded.setRemoteId(remoteKey);
        TaskSaveResult result2 = connector.saveData(Arrays.asList(loaded), null, mappings);
        assertFalse(result2.hasErrors());
        assertEquals(1, result2.getUpdatedTasksNumber());

        GTask loadedAgain = connector.loadTaskByKey(remoteKey, mappings);
        assertEquals(NEW_SUMMARY, loadedAgain.getSummary());
    }
}