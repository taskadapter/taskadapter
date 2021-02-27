package com.taskadapter.connector.testlib;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.NewConnector;
import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.definition.TaskKeyMapping;
import com.taskadapter.core.PreviouslyCreatedTasksCache;
import com.taskadapter.core.PreviouslyCreatedTasksResolver;
import com.taskadapter.model.GTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Keeps old task -> new task cache that can be used for "update" operations in tests.
 */
public class StatefulTestTaskSaver {
    private final NewConnector connector;
    private final String targetLocation;

    private final List<TaskKeyMapping> taskIds = new ArrayList<>();

    public StatefulTestTaskSaver(NewConnector connector, String targetLocation) {
        this.connector = connector;
        this.targetLocation = targetLocation;
    }

    public GTask saveAndLoad(GTask task, List<FieldRow<?>> rows) {
        var cache = new PreviouslyCreatedTasksCache("1", targetLocation, taskIds);
        var resolver = new PreviouslyCreatedTasksResolver(cache);
        var result = connector.saveData(resolver, List.of(task), ProgressMonitorUtils.DUMMY_MONITOR, rows);

        var ids = result.getKeyToRemoteKeyList().get(0);
        var newTaskId = ids.newId;
        taskIds.add(new TaskKeyMapping(newTaskId, newTaskId));

        var remoteKeys = result.getRemoteKeys();
        var remoteKey = remoteKeys.iterator().next();
        return connector.loadTaskByKey(remoteKey, rows);
    }
}
