package com.taskadapter.connector.testlib;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.definition.TaskSaveResult;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.core.NewConnector;
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
        TaskSaveResult taskSaveResult = connector.saveData(Arrays.asList(task), null, rows);
        String newKey = taskSaveResult.getRemoteKeys().iterator().next();
        return connector.loadTaskByKey(newKey, rows);
    }
}
