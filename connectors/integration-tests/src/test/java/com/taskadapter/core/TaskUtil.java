package com.taskadapter.core;

import com.taskadapter.model.GTask;

public class TaskUtil {

    /**
     * set remoteId = key.
     */
    public static void setRemoteIdField(java.util.List<GTask> tasks) {
        for (GTask task : tasks) {
            task.setSourceSystemId(task.getKey());

            if (!task.getChildren().isEmpty()) {
                setRemoteIdField(task.getChildren());
            }
        }
    }

}
