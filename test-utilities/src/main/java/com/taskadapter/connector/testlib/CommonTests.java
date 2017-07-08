package com.taskadapter.connector.testlib;

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

    public static void testCreates2Tasks(NewConnector connector, List<FieldRow> rows) throws ConnectorException {
        int tasksQty = 2;
        List<GTask> tasks = TestUtils.generateTasks(tasksQty);
        TaskSaveResult result = connector.saveData(tasks, ProgressMonitorUtils.DUMMY_MONITOR, rows);
        assertFalse(result.hasErrors());
        assertEquals(tasksQty, result.getCreatedTasksNumber());
    }

    public static void taskCreatedAndUpdatedOK(NewConnector connector, List<FieldRow> rows) throws ConnectorException {
        int tasksQty = 1;
        GTask task = TestUtils.generateTask();

        Integer id = task.getId();

        // CREATE
        TaskSaveResult result = connector.saveData(Arrays.asList(task), ProgressMonitorUtils.DUMMY_MONITOR, rows);
        assertFalse(result.hasErrors());
        assertEquals(tasksQty, result.getCreatedTasksNumber());
        String remoteKey = result.getRemoteKey(id);

        GTask loaded = connector.loadTaskByKey(remoteKey, rows);

        // UPDATE
        String NEW_SUMMARY = "new summary here";
        loaded.setSummary(NEW_SUMMARY);
        loaded.setRemoteId(remoteKey);
        TaskSaveResult result2 = connector.saveData(Arrays.asList(loaded), ProgressMonitorUtils.DUMMY_MONITOR, rows);
        assertFalse(result2.hasErrors());
        assertEquals(1, result2.getUpdatedTasksNumber());

        GTask loadedAgain = connector.loadTaskByKey(remoteKey, rows);
        assertEquals(NEW_SUMMARY, loadedAgain.getSummary());
    }*/
}