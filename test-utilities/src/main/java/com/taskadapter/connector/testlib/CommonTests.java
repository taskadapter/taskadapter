package com.taskadapter.connector.testlib;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.NewConnector;
import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.definition.TaskSaveResult;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public final class CommonTests {
    // TODO TA3 fix
/*    public static void testLoadTasks(NewConnector connector, List<FieldRow> rows) throws ConnectorException {
        int tasksQty = 1;
        List<GTask> tasks = TestUtils.generateTasks(tasksQty);

        String expectedSummaryTask1 = tasks.get(0).getSummary();
        Integer expectedID = tasks.get(0).getId();

        TaskSaveResult result = connector.saveData(tasks, ProgressMonitorUtils.DUMMY_MONITOR, rows);
        assertEquals(tasksQty, result.getCreatedTasksNumber());

        Integer createdTask1Id = Integer.valueOf(result.getIdToRemoteKeyMap().get(expectedID));

        List<GTask> loadedTasks = ConnectorUtils.loadDataOrderedById(connector, rows);
        // there could be some other previously created tasks
        assertTrue(loadedTasks.size() >= tasksQty);

        GTask foundTask = TestUtils.findTaskInList(loadedTasks, createdTask1Id);
        assertNotNull(foundTask);
        assertEquals(expectedSummaryTask1, foundTask.getSummary());
    }


    public static void descriptionSavedByDefault(NewConnector connector, List<FieldRow> rows) throws ConnectorException {
        GTask task = TestUtils.generateTask();
        GTask loadedTask = TestUtils.saveAndLoadViaSummary(connector, task, rows);
        assertEquals(task.getDescription(), loadedTask.getDescription());
    }

    public static void descriptionSavedIfSelected(NewConnector connector, List<FieldRow> rows) throws ConnectorException {
        GTask task = TestUtils.generateTask();
        GTask loadedTask = new TestSaver(connector, rows).saveAndLoad(task);
        assertEquals(task.getDescription(), loadedTask.getDescription());
    }
*/
    public static void createsTasks(NewConnector connector, List<FieldRow> rows, List<GTask> tasks) throws ConnectorException {
        TaskSaveResult result = connector.saveData(tasks, ProgressMonitorUtils.DUMMY_MONITOR, rows);
        assertFalse(result.hasErrors());
        assertEquals(tasks.size(), result.getCreatedTasksNumber());
    }

    /**
     * TODO TA3 this requires remote ID support.
     */
    public static void taskCreatedAndUpdatedOK(NewConnector connector, List<FieldRow> rows, GTask task,
                                               String fieldToChangeInTest) throws ConnectorException {
        Integer id = task.getId();

        // CREATE
        TaskSaveResult result = connector.saveData(Arrays.asList(task), ProgressMonitorUtils.DUMMY_MONITOR, rows);
        assertFalse(result.hasErrors());
        assertEquals(1, result.getCreatedTasksNumber());
        String remoteKey = result.getRemoteKey(id);

        GTask loaded = connector.loadTaskByKey(remoteKey, rows);

        // UPDATE
        String newValue = "some new text";
        loaded.setValue(fieldToChangeInTest, newValue);
        loaded.setRemoteId(remoteKey);
        TaskSaveResult result2 = connector.saveData(Arrays.asList(loaded), ProgressMonitorUtils.DUMMY_MONITOR, rows);
        assertFalse(result2.hasErrors());
        assertEquals(1, result2.getUpdatedTasksNumber());

        GTask loadedAgain = connector.loadTaskByKey(remoteKey, rows);
        assertEquals(newValue, loadedAgain.getValue(fieldToChangeInTest));
    }
}