package com.taskadapter.connector.definition;

import java.util.Map;

/**
 * Task save result.
 * 
 * @author maxkar
 * 
 */
public final class TaskSaveResult {
    // TODO this is a temporary solution to enable "download" link after
    // exporting to MSP in server mode. refactor!
    private final String targetFileAbsolutePath;

    /**
     * Number of updated tasks.
     */
    private final int updatedTasksNumber;

    /**
     * Number of created tasks.
     */
    private final int createdTasksNumber;

    // maps ID --> remote KEY when new tasks are created
    private final Map<Integer, String> idToRemoteKeyMap;

    public TaskSaveResult(String targetFileAbsolutePath,
            int updatedTasksNumber, int createdTasksNumber,
            Map<Integer, String> idToRemoteKeyMap) {
        super();
        this.targetFileAbsolutePath = targetFileAbsolutePath;
        this.updatedTasksNumber = updatedTasksNumber;
        this.createdTasksNumber = createdTasksNumber;
        this.idToRemoteKeyMap = idToRemoteKeyMap;
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

    public Map<Integer, String> getIdToRemoteKeyMap() {
        return idToRemoteKeyMap;
    }
    
    public String getRemoteKey(Integer id) {
        return idToRemoteKeyMap.get(id);
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

}
