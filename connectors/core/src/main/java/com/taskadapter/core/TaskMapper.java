package com.taskadapter.core;

import java.util.Collection;

import com.taskadapter.connector.definition.NewMappings;
import com.taskadapter.model.GTask;

/**
 * Task mapping utilities.
 * 
 */
public final class TaskMapper {

    private TaskMapper() {
        throw new UnsupportedOperationException();
    }

    /**
     * Updates tasks fields based on mappings.
     * 
     * @param mappings
     *            current mappings.
     * @param tasks
     *            tasks to update.
     */
    public static void remap(NewMappings mappings, Collection<GTask> tasks) {
        if (tasks == null) {
            return;
        }
        for (GTask task : tasks) {
            remap(mappings, task);
        }
    }

    /**
     * Updates task fields based on mappings.
     * 
     * @param mappings
     *            current mappings.
     * @param tasks
     *            tasks to update.
     */
    private static void remap(NewMappings mappings, GTask task) {
        /*
         * Current implementation has fixed mappings and different load/storage
         * configurations.
         */
        final Integer oldId = task.getId();
        final String oldRemoteId = task.getRemoteId();
        task.setId(oldRemoteId == null ? null : Integer.parseInt(oldRemoteId));
        task.setRemoteId(oldId.toString());
        remap(mappings, task.getChildren());
    }
}
