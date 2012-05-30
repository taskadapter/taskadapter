package com.taskadapter.connector.common;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.SyncResult;
import com.taskadapter.model.GTask;

import java.util.List;

public interface TaskSaver<T extends ConnectorConfig> {
    public SyncResult saveData(List<GTask> tasks, ProgressMonitor monitor);
}
