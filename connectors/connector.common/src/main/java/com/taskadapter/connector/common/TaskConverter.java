package com.taskadapter.connector.common;

import java.util.List;

import com.taskadapter.model.GTask;

public interface TaskConverter<T> {

	List<GTask> convertToGenericTaskList(List<T> tasks);

	GTask convertToGenericTask(T task);
}
