package com.taskadapter.connector.testlib;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.NewConnector;
import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.core.PreviouslyCreatedTasksResolver;
import com.taskadapter.core.TaskSaver;
import com.taskadapter.model.Field;
import com.taskadapter.model.GTask;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class CommonTestChecks {
    public static Function<TaskId, Void> skipCleanup = taskId -> null;

    public static void fieldIsSavedByDefault(NewConnector connector, GTask task,
                                             List<Field<?>> fields,
                                             Field<?> fieldToSearch,
                                             java.util.function.Function<TaskId, Void> cleanup) {
        try {
            var rows = FieldRowBuilder.rows(fields);
            var loadedTask = TestUtils.saveAndLoadViaSummary(connector, task, rows, fieldToSearch);
            assertThat(loadedTask.getValue(fieldToSearch)).isEqualTo(task.getValue(fieldToSearch));
            cleanup.apply(loadedTask.getIdentity());
        } catch (Exception e) {
            fail("failed with exception " + e.toString());
        }
    }

    public static <T> void taskCreatedAndUpdatedOK(String targetLocation,
                                                   NewConnector connector,
                                                   List<FieldRow<?>> rows,
                                                   GTask task,
                                                   List<FieldWithValue<T>> toUpdate,
                                                   Function<TaskId, Void> cleanup) {
        // CREATE
        var result = TaskSaver.save(PreviouslyCreatedTasksResolver.empty, connector,
                "some name", rows, Arrays.asList(task), ProgressMonitorUtils.DUMMY_MONITOR);
        assertFalse("must not have any errors, but got " + result.getTaskErrors(), result.hasErrors());
        assertEquals(1, result.getCreatedTasksNumber());
        var newTaskId = result.getRemoteKeys().iterator().next();
        try {
            GTask loaded = connector.loadTaskByKey(newTaskId, rows);
            // UPDATE all requested fields
            toUpdate.forEach(i -> loaded.setValue(i.field, i.value));
            var resolver = new TaskResolverBuilder(targetLocation).pretend(newTaskId, newTaskId);
            var result2 = TaskSaver.save(resolver, connector, "some name",
                    rows, Arrays.asList(loaded), ProgressMonitorUtils.DUMMY_MONITOR);
            assertFalse(result2.hasErrors());
            assertEquals(1, result2.getUpdatedTasksNumber());
            var loadedAgain = connector.loadTaskByKey(newTaskId, rows);

            toUpdate.forEach(i -> assertEquals(i.value, loadedAgain.getValue(i.field)));

            cleanup.apply(loaded.getIdentity());
        } catch (ConnectorException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void taskCreatedAndUpdatedOK(String targetLocation, NewConnector connector,
                                                   List<FieldRow<?>> rows, GTask task,
                                                   Field<T> fieldToChangeInTest,
                                                   T newValue,
                                                   Function<TaskId, Void> cleanup) {
        taskCreatedAndUpdatedOK(targetLocation, connector, rows, task, List.of(new FieldWithValue<>(fieldToChangeInTest, newValue)),
                cleanup);
    }

    public static void taskIsCreatedAndLoaded(NewConnector connector, GTask task,
                                              List<FieldRow<?>> rows,
                                              List<Field<?>> fields,
                                              Function<TaskId, Void> cleanup) {
        var createdTask = createAndLoadTask(connector, task, rows);
        // check all requested fields
        fields.forEach(f ->
                assertThat(createdTask.getValue(f)).isEqualTo(task.getValue(f))
        );

        cleanup.apply(createdTask.getIdentity());
    }

    public static GTask createAndLoadTask(NewConnector connector, GTask task,
                                          List<FieldRow<?>> rows) {
        var result = connector.saveData(PreviouslyCreatedTasksResolver.empty, List.of(task),
                ProgressMonitorUtils.DUMMY_MONITOR,
                rows);
        var createdTask1Id = result.getRemoteKeys().iterator().next();

        try {
            var loadedTasks = connector.loadData();
            // there could be some other previously created tasks
            assertThat(loadedTasks.size()).isGreaterThanOrEqualTo(1);

            var foundTask = TestUtils.findTaskInList(loadedTasks, createdTask1Id);
            assertThat(foundTask).isPresent();
            return foundTask.get();
        } catch (ConnectorException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createsTasks(NewConnector connector,
                                    List<FieldRow<?>> rows,
                                    List<GTask> tasks,
                                    Function<TaskId, Void> cleanup) {
        var result = connector.saveData(PreviouslyCreatedTasksResolver.empty, tasks, ProgressMonitorUtils.DUMMY_MONITOR, rows);
        assertFalse(result.hasErrors());
        assertEquals(tasks.size(), result.getCreatedTasksNumber());
//        logger.debug(s"created $result");
        result.getRemoteKeys().forEach(cleanup::apply);
    }

}
