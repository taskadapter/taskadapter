package com.taskadapter.connector.definition;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class TaskSaveResult {
    // TODO this is a temporary solution to enable "download" link after
    // exporting to MSP in server mode. refactor!
    private final String targetFileAbsolutePath;

    private final int updatedTasksNumber;

    private final int createdTasksNumber;

    // maps ID --> remote KEY when new tasks are created
    private final Map<Long, TaskId> idToRemoteKeyMap;
    
    private final List<Throwable> generalErrors;
    
    private final List<TaskError<Throwable>> taskErrors;

    public TaskSaveResult(String targetFileAbsolutePath,
            int updatedTasksNumber, int createdTasksNumber,
            Map<Long, TaskId> idToRemoteKeyMap, List<Throwable> generalErrors,
            List<TaskError<Throwable>> taskErrors) {
        super();
        this.targetFileAbsolutePath = targetFileAbsolutePath;
        this.updatedTasksNumber = updatedTasksNumber;
        this.createdTasksNumber = createdTasksNumber;
        this.idToRemoteKeyMap = idToRemoteKeyMap;
        this.generalErrors = generalErrors;
        this.taskErrors = taskErrors;
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

    public Map<Long, TaskId> getIdToRemoteKeyMap() {
        return idToRemoteKeyMap;
    }
    
    public TaskId getRemoteKey(Long id) {
        return idToRemoteKeyMap.get(id);
    }

    public Collection<TaskId> getRemoteKeys() {
        return idToRemoteKeyMap.values();
    }
    
    public List<Throwable> getGeneralErrors() {
        return generalErrors;
    }

    public List<TaskError<Throwable>> getTaskErrors() {
        return taskErrors;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + createdTasksNumber;
        result = prime
                * result
                + ((idToRemoteKeyMap == null) ? 0 : idToRemoteKeyMap.hashCode());
        result = prime
                * result
                + ((targetFileAbsolutePath == null) ? 0
                        : targetFileAbsolutePath.hashCode());
        result = prime * result + updatedTasksNumber;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TaskSaveResult other = (TaskSaveResult) obj;
        if (createdTasksNumber != other.createdTasksNumber)
            return false;
        if (idToRemoteKeyMap == null) {
            if (other.idToRemoteKeyMap != null)
                return false;
        } else if (!idToRemoteKeyMap.equals(other.idToRemoteKeyMap))
            return false;
        if (targetFileAbsolutePath == null) {
            if (other.targetFileAbsolutePath != null)
                return false;
        } else if (!targetFileAbsolutePath.equals(other.targetFileAbsolutePath))
            return false;
        if (updatedTasksNumber != other.updatedTasksNumber)
            return false;
        return true;
    }

    public boolean hasErrors() {
        return !generalErrors.isEmpty() || !taskErrors.isEmpty();
    }

    @Override
    public String toString() {
        return "TaskSaveResult{" +
                "targetFileAbsolutePath='" + targetFileAbsolutePath + '\'' +
                ", updatedTasksNumber=" + updatedTasksNumber +
                ", createdTasksNumber=" + createdTasksNumber +
                ", idToRemoteKeyMap=" + idToRemoteKeyMap +
                ", generalErrors=" + generalErrors +
                ", taskErrors=" + taskErrors +
                '}';
    }
}
