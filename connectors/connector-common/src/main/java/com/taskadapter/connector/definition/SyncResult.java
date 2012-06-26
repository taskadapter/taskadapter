package com.taskadapter.connector.definition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @param <E> error result type.
 */
public class SyncResult<E> {
    // TODO this is a temporary solution to enable "download" link after exporting to MSP in server mode. refactor!
    private String targetFileAbsolutePath;

    private int updatedTasksNumber;
    private int createdTasksNumber;

    // maps ID --> remote KEY when new tasks are created
    private Map<Integer, String> idToRemoteKeyMap = new HashMap<Integer, String>();
    private List<TaskError<E>> errors = new ArrayList<TaskError<E>>();
    private List<E> generalErrors = new ArrayList<E>();

    public String getTargetFileAbsolutePath() {
        return targetFileAbsolutePath;
    }

    public void setTargetFileAbsolutePath(String targetFileAbsolutePath) {
        this.targetFileAbsolutePath = targetFileAbsolutePath;
    }

    public void addError(TaskError<E> e) {
        errors.add(e);
    }

    public void addGeneralError(E e) {
        generalErrors.add(e);
    }

    /**
     * @return errors list, never NULL
     */
    public List<TaskError<E>> getErrors() {
        return errors;
    }

    public void addCreatedTask(Integer originalId, String newId) {
        idToRemoteKeyMap.put(originalId, newId);
        createdTasksNumber++;
    }

    public int getCreateTasksNumber() {
        return createdTasksNumber;
    }

    public void addUpdatedTask(Integer originalId, String newId) {
        idToRemoteKeyMap.put(originalId, newId);
        updatedTasksNumber++;
    }

    public int getUpdatedTasksNumber() {
        return updatedTasksNumber;
    }

    public String getRemoteKey(Integer id) {
        return idToRemoteKeyMap.get(id);
    }

    public boolean hasErrors() {
        return ((!generalErrors.isEmpty()) || (!errors.isEmpty()));
    }

    public List<E> getGeneralErrors() {
        return generalErrors;
    }
}
