package com.taskadapter.connector.testlib;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.NewConnector;
import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.definition.SaveResult;
import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.core.PreviouslyCreatedTasksResolver;
import com.taskadapter.model.GTask;

import java.util.Arrays;
import java.util.List;

public class TestSaver {
    private NewConnector connector;
    private Iterable<FieldRow<?>> rows;

    public TestSaver(NewConnector connector, List<FieldRow<?>> rows) {
        this.connector = connector;
        this.rows = rows;
    }

    public GTask saveAndLoad(GTask task) {
        SaveResult taskSaveResult = connector.saveData(PreviouslyCreatedTasksResolver.empty(),
                Arrays.asList(task),
                ProgressMonitorUtils.DUMMY_MONITOR,
                rows);
        TaskId newKey = taskSaveResult.getRemoteKeys().iterator().next();
        return connector.loadTaskByKey(newKey, rows);
    }
}
