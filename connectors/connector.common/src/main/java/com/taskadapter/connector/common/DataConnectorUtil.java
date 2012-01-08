package com.taskadapter.connector.common;

import com.taskadapter.model.GTask;

import java.util.List;

public class DataConnectorUtil {

	/**
	 * @param tasks
	 * @return total number of tasks, including all children
	 */
	public static int calculateNumberOfTasks(List<GTask> tasks) {
		int counter = 0;
        for (GTask task : tasks) {
            counter++;
            counter += calculateNumberOfTasks(task.getChildren());
        }
		return counter;
	}
	
}
