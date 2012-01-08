package com.taskadapter.connector.definition;

import java.util.List;

import com.taskadapter.model.GTask;

public interface FileBasedConnector {
    public void updateTasksByRemoteIds(List<GTask> tasks);
    // TODO use it. see how it's done in Eclipse branch
    public boolean fileExists();
    // TODO use it. see how it's done in Eclipse branch
    public void validateCanUpdate() throws ValidationException;
}