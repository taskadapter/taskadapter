package com.taskadapter.connector.definition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.taskadapter.model.GTask;

public final class TaskSaveResultBuilder {
    private String targetFileAbsolutePath;

    /**
     * Number of updated tasks.
     */
    private int updatedTasksNumber;

    /**
     * Number of created tasks.
     */
    private int createdTasksNumber;

    // maps original task Key --> new task info when new tasks are created
    private final Map<Long, TaskId> idToRemoteKeyMap = new HashMap<>();
    
    private final List<Throwable> generalErrors = new ArrayList<>();
    
    private final List<TaskError<Throwable>> taskErrors = new ArrayList<>();

    public void setTargetFileAbsolutePath(String targetFileAbsolutePath) {
        this.targetFileAbsolutePath = targetFileAbsolutePath;
    }
    
    public void addCreatedTask(Long originalId, TaskId newKey) {
        idToRemoteKeyMap.put(originalId, newKey);
        createdTasksNumber++;
    }

    public void addUpdatedTask(Long originalId, TaskId newId) {
        idToRemoteKeyMap.put(originalId, newId);
        updatedTasksNumber++;
    }
    
    public TaskId getRemoteKey(Long originalId) {
        return idToRemoteKeyMap.get(originalId);
    }
    
    public void addGeneralError(Throwable e) {
        generalErrors.add(e);
    }
    
    public void addTaskError(GTask task, Throwable e) {
        taskErrors.add(new TaskError<>(task, e));
    }

    public TaskSaveResult getResult() {
        return new TaskSaveResult(targetFileAbsolutePath, updatedTasksNumber,
                createdTasksNumber,
                Collections.unmodifiableMap(new HashMap<>(
                        idToRemoteKeyMap)),
                Collections.unmodifiableList(generalErrors),
                Collections.unmodifiableList(taskErrors));
    }
}
