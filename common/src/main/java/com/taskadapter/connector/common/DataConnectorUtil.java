package com.taskadapter.connector.common;

import com.taskadapter.model.GTask;

import java.util.Collection;

public class DataConnectorUtil {

    /**
     * @return total number of tasks, including all children
     */
    public static int calculateNumberOfTasks(Collection<GTask> tasks) {
        int counter = 0;
        for (GTask task : tasks) {
            counter++;
            counter += calculateNumberOfTasks(task.getChildren());
        }
        return counter;
    }

}
