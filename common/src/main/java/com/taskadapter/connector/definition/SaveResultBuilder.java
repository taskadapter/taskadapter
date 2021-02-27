package com.taskadapter.connector.definition;

import com.taskadapter.model.GTask;

import java.util.ArrayList;
import java.util.List;

public class SaveResultBuilder {

    private String targetFileAbsolutePath = "";
    /**
     * Number of updated tasks.
     */
    private int updatedTasksNumber = 0;
    /**
     * Number of created tasks.
     */
    private int createdTasksNumber = 0;

    // maps original task Key --> new task info when new tasks are created
    private final List<TaskKeyMapping> idToRemoteKeyMap = new ArrayList<>();
    private final List<Throwable> generalErrors = new ArrayList<>();
    private final List<TaskError> taskErrors = new ArrayList<>();

    public void setTargetFileAbsolutePath(String targetFileAbsolutePath) {
        this.targetFileAbsolutePath = targetFileAbsolutePath;
    }

    public void addCreatedTask(TaskId original, TaskId newKey) {
        idToRemoteKeyMap.add(new TaskKeyMapping(original, newKey));
        createdTasksNumber += 1;
    }

    public void addUpdatedTask(TaskId original, TaskId newId) {
        idToRemoteKeyMap.add(new TaskKeyMapping(original, newId));
        updatedTasksNumber += 1;
    }

    public TaskId getRemoteKey(TaskId original) {
        return idToRemoteKeyMap.stream()
                .filter(lon -> lon.originalId.equals(original))
                .map(e -> e.newId)
                .findFirst()
                .orElse(null);
    }

    public void addGeneralError(Throwable e) {
        generalErrors.add(e);
    }

    public void addTaskError(GTask task, Exception e) {
        taskErrors.add(new TaskError(task, e));
    }

    public SaveResult getResult() {
        return new SaveResult(targetFileAbsolutePath, updatedTasksNumber, createdTasksNumber,
                idToRemoteKeyMap,
                generalErrors,
                taskErrors
        );
    }
}
