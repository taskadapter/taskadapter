package com.taskadapter.connector.testlib;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.definition.TaskSaveResult;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.NewConnector;
import com.taskadapter.model.GTask;

import java.util.Arrays;
import java.util.List;

public class TestSaver {
    private NewConnector connector;
    private List<FieldRow> rows;

    public TestSaver(NewConnector connector, List<FieldRow> rows) {
        this.connector = connector;
        this.rows = rows;
    }

    public GTask saveAndLoad(GTask task) throws ConnectorException {
        TaskSaveResult taskSaveResult = connector.saveData(new InMemoryTaskKeeper(),
                Arrays.asList(task), ProgressMonitorUtils.DUMMY_MONITOR, rows);
        String newKey = taskSaveResult.getRemoteKeys().iterator().next().key();
        return connector.loadTaskByKey(newKey, rows);
    }
}
