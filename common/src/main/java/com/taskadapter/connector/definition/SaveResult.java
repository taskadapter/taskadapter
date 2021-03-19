package com.taskadapter.connector.definition;

import java.util.List;
import java.util.stream.Collectors;

public class SaveResult {
    private String targetFileAbsolutePath;
    private int updatedTasksNumber;
    private int createdTasksNumber;
    private List<TaskKeyMapping> keyToRemoteKeyList;
    private List<Throwable> generalErrors;
    private List<TaskError> taskErrors;

    public SaveResult(String targetFileAbsolutePath, int updatedTasksNumber, int createdTasksNumber,
                      List<TaskKeyMapping> keyToRemoteKeyList, List<Throwable> generalErrors, List<TaskError> taskErrors) {
        this.targetFileAbsolutePath = targetFileAbsolutePath;
        this.updatedTasksNumber = updatedTasksNumber;
        this.createdTasksNumber = createdTasksNumber;
        this.keyToRemoteKeyList = keyToRemoteKeyList;
        this.generalErrors = generalErrors;
        this.taskErrors = taskErrors;
    }

    public static SaveResult withError(Exception e) {
        return new SaveResult("", 0, 0, List.of(),
                List.of(e),
                List.of());
    }

    public List<TaskId> getRemoteKeys() {
        return keyToRemoteKeyList.stream().map(taskKeyMapping -> taskKeyMapping.newId)
                .collect(Collectors.toList());
    }

    public boolean hasErrors() {
        return !generalErrors.isEmpty() || !taskErrors.isEmpty();
    }

    public String getTargetFileAbsolutePath() {
        return targetFileAbsolutePath;
    }

    public int getUpdatedTasksNumber() {
        return updatedTasksNumber;
    }

    public int getCreatedTasksNumber() {
        return createdTasksNumber;
    }

    public List<TaskKeyMapping> getKeyToRemoteKeyList() {
        return keyToRemoteKeyList;
    }

    public List<Throwable> getGeneralErrors() {
        return generalErrors;
    }

    public List<TaskError> getTaskErrors() {
        return taskErrors;
    }
}
