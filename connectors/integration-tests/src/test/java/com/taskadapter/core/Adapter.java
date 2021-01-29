package com.taskadapter.core;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.NewConnector;
import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.definition.SaveResult;
import com.taskadapter.model.GTask;

import java.util.List;

class Adapter {
    final NewConnector connector1;
    final NewConnector connector2;

    public Adapter(NewConnector connector1, NewConnector connector2) {
        this.connector1 = connector1;
        this.connector2 = connector2;
    }

    SaveResult adapt(List<FieldRow<?>> rows) {
        List<GTask> tasks = connector1.loadData();
        return connector2.saveData(PreviouslyCreatedTasksResolver.empty(), tasks, ProgressMonitorUtils.DUMMY_MONITOR, rows);
    }
}

