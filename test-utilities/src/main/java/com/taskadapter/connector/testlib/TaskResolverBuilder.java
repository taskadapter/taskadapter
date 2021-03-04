package com.taskadapter.connector.testlib;

import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.connector.definition.TaskKeyMapping;
import com.taskadapter.core.PreviouslyCreatedTasksCache;
import com.taskadapter.core.PreviouslyCreatedTasksResolver;

public class TaskResolverBuilder {
    String targetLocation;

    public TaskResolverBuilder(String targetLocation) {
        this.targetLocation = targetLocation;
    }

    public PreviouslyCreatedTasksResolver pretend(TaskId id1, TaskId id2) {
        return new PreviouslyCreatedTasksResolver(
                new PreviouslyCreatedTasksCache("1", targetLocation, java.util.Arrays.asList(
                        new TaskKeyMapping(id1, id2)
                ))
        );
    }
}


