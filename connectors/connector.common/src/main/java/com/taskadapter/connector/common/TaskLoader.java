package com.taskadapter.connector.common;

import com.taskadapter.connector.definition.ConnectorConfig;
import com.taskadapter.model.GTask;

import java.util.List;

public interface TaskLoader<T extends ConnectorConfig> {
	List<GTask> loadTasks(T config) throws Exception;
	GTask loadTask(T config, String taskKey);
    void beforeTasksLoad(T config);
}
