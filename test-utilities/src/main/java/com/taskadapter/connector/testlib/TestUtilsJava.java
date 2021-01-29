package com.taskadapter.connector.testlib;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.NewConnector;
import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.definition.SaveResult;
import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.core.PreviouslyCreatedTasksResolver;
import com.taskadapter.core.TaskLoader;
import com.taskadapter.model.GTask;
import scala.collection.Seq;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.taskadapter.model.GTaskUtils.ID_COMPARATOR;

public class TestUtilsJava {

    /**
     * Uses a NEW instance of PreviouslyCreatedTasksResolver (empty) for each call, so this won't work for some tests
     * that require updates.
     */
    public static GTask saveAndLoad(NewConnector connector, GTask task, List<FieldRow<?>> rows) {
        SaveResult result = connector.saveData(PreviouslyCreatedTasksResolver.empty(), Arrays.asList(task), ProgressMonitorUtils.DUMMY_MONITOR, rows);
        Seq<TaskId> remoteKeys = result.getRemoteKeys();
        TaskId remoteKey = remoteKeys.iterator().next();
        return connector.loadTaskByKey(remoteKey, rows);
    }

    /**
     * @param rows source-target field rows
     */
    public static GTask loadAndSave(NewConnector sourceConnector, NewConnector targetConnector,
                                    List<FieldRow<?>> rows) throws ConnectorException {
        GTask loadedTask = TaskLoader.loadTasks(1, sourceConnector, "sourceName", ProgressMonitorUtils.DUMMY_MONITOR).get(0);
        GTask result = saveAndLoadList(targetConnector, Arrays.asList(loadedTask), rows).get(0);
        return result;
    }

    public static List<GTask> loadAndSaveList(NewConnector sourceConnector, NewConnector targetConnector,
                                              List<FieldRow<?>> rows) throws ConnectorException {
        List<GTask> loadedTasks = TaskLoader.loadTasks(1000, sourceConnector, "sourceName", ProgressMonitorUtils.DUMMY_MONITOR);
        List<GTask> result = saveAndLoadList(targetConnector, loadedTasks, rows);
        return result;
    }

    public static List<GTask> saveAndLoadList(NewConnector connector, List<GTask> tasks, List<FieldRow<?>> rows) throws ConnectorException {
        connector.saveData(PreviouslyCreatedTasksResolver.empty(), tasks, ProgressMonitorUtils.DUMMY_MONITOR, rows);
        List<GTask> list = connector.loadData();

        Collections.sort(list, ID_COMPARATOR);

        return list;
    }

    /**
     * Load task that was previously created and its result is saved in [[SaveResult]]
     */
    public static GTask loadCreatedTask(NewConnector connector, List<FieldRow<?>> rows, SaveResult result) {
        TaskId remoteKey = result.getRemoteKeys().head();
        return connector.loadTaskByKey(remoteKey, rows);
    }


    public static TaskId save(NewConnector connector, GTask task, List<FieldRow<?>> rows) throws ConnectorException {
        SaveResult result = connector.saveData(PreviouslyCreatedTasksResolver.empty(), Arrays.asList(task), ProgressMonitorUtils.DUMMY_MONITOR, rows);
        Seq<TaskId> remoteKeys = result.getRemoteKeys();
        return remoteKeys.iterator().next();
    }

    public static Optional<GTask> findTaskInList(List<GTask> list, TaskId createdTaskId) {
        return list.stream().filter(t -> t.getIdentity().equals(createdTaskId)).findFirst();
    }
}
