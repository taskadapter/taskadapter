package com.taskadapter.connector.definition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncResult {
    private String message;

    private int updatedTasksNumber;
    private int createdTasksNumber;

    // maps ID --> remote KEY when new tasks are created
    private Map<Integer, String> idToRemoteKeyMap = new HashMap<Integer, String>();
    private List<TaskError> errors = new ArrayList<TaskError>();
    private List<String> generalErrors = new ArrayList<String>();

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void addError(TaskError e) {
        errors.add(e);
    }

    public void addGeneralError(String e) {
        generalErrors.add(e);
    }

    /**
     * @return errors list, never NULL
     */
    public List<TaskError> getErrors() {
        return errors;
    }

    public void addCreatedTask(Integer originalId, String newId) {
        idToRemoteKeyMap.put(originalId, newId);
        createdTasksNumber++;
    }

    public int getCreateTasksNumber() {
        return createdTasksNumber;
    }

    public void addUpdatedTask() {
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

    public List<String> getGeneralErrors() {
        return generalErrors;
    }
}
