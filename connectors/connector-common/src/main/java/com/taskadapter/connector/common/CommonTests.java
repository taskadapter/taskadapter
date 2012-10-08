package com.taskadapter.connector.common;

import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.SyncResult;
import com.taskadapter.connector.definition.TaskErrors;
import com.taskadapter.connector.definition.TaskSaveResult;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor.FIELD;

import java.util.List;

public class CommonTests {

    public void testLoadTasks(Connector<?> connector) throws ConnectorException {
        // TODO !!!
/*
        int tasksQty = 1;
        List<GTask> tasks = TestUtils.generateTasks(tasksQty);

        String expectedSummaryTask1 = tasks.get(0).getSummary();
        Integer expectedID = tasks.get(0).getId();

        SyncResult<TaskSaveResult, TaskErrors<Throwable>> result = connector.saveData(tasks, null, mappings);
        assertEquals(tasksQty, result.getResult().getCreatedTasksNumber());

        Integer createdTask1Id = Integer.valueOf(result.getResult().getIdToRemoteKeyMap().get(expectedID));

        List<GTask> loadedTasks = ConnectorUtils.loadDataOrderedById(connector);
        // there could be some other previously created tasks
        assertTrue(loadedTasks.size() >= tasksQty);

        GTask foundTask = TestUtils.findTaskInList(loadedTasks, createdTask1Id);
        assertNotNull(foundTask);
        assertEquals(expectedSummaryTask1, foundTask.getSummary());
*/
    }

    public void descriptionSavedByDefault(Connector<?> connector) throws ConnectorException {
    // TODO !!!
    /*
        GTask task = TestUtils.generateTask();
        GTask loadedTask = TestUtils.saveAndLoadViaSummary(connector, task, mappings);
        assertEquals(task.getDescription(), loadedTask.getDescription());
*/
    }

    public void descriptionSavedIfSelected(Connector<?> connector) throws ConnectorException {
        // TODO !!!
/*
        GTask task = TestUtils.generateTask();
        GTask loadedTask = new TestSaver(connector).selectField(FIELD.DESCRIPTION).saveAndLoad(task, mappings);
        assertEquals(task.getDescription(), loadedTask.getDescription());
*/
    }

    public void testCreates2Tasks(Connector<?> connector) throws ConnectorException {
        // TODO !!!
/*
        int tasksQty = 2;
        List<GTask> tasks = TestUtils.generateTasks(tasksQty);
        SyncResult<TaskSaveResult, TaskErrors<Throwable>> result = connector.saveData(tasks, null, mappings);
        assertFalse("Errors: " + result.getErrors().toString(), result.getErrors().hasErrors());
        assertEquals(tasksQty, result.getResult().getCreatedTasksNumber());
*/
    }

    private void assertNotNull(GTask foundTask) {
        if (foundTask == null) {
            throw new RuntimeException("value must be not null");
        }
    }

    private void assertFalse(String msg, boolean b) {
        if (b) {
            throw new RuntimeException(msg);
        }
    }

    private void assertTrue(boolean b) {
        if (!b) {
            throw new RuntimeException("expected: true, actual: false");
        }
    }

    /**
     * this mimics JUnit behavior so we don't have to add junit bundle dependency to this module.
     */
    private void assertEquals(Object expected, Object actual) {
        if ((expected == null) && (actual != null)) {
            throw new RuntimeException("validation failed. expected:" + expected + " actual:" + actual);
        }
        if ((actual == null) && (expected != null)) {
            throw new RuntimeException("validation failed. expected:" + expected + " actual:" + actual);
        }
        if ((expected != null) && (actual != null) && (!expected.equals(actual))) {
            throw new RuntimeException("validation failed. expected:" + expected + " actual:" + actual);
        }
    }

}
