package com.taskadapter.core;

import java.util.Iterator;

import com.taskadapter.model.GTask;

public class TaskUtil {

    /**
     * set remoteId = key.
     */
    public static void setRemoteIdField(java.util.List<GTask> tasks) {
        for (Iterator<GTask> iterator = tasks.iterator(); iterator.hasNext(); ) {
            GTask task = iterator.next();
            task.setRemoteId(task.getKey());

            if (!task.getChildren().isEmpty()) {
                setRemoteIdField(task.getChildren());
            }
        }
    }

}
