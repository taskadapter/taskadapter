package com.taskadapter.connector.definition;

import java.util.List;

import com.taskadapter.model.GTask;

public interface FileBasedConnector {
    void updateTasksByRemoteIds(List<GTask> tasks);

    boolean fileExists();

    // TODO use it. see how it's done in Eclipse branch
    void validateCanUpdate() throws ValidationException;

    String getAbsoluteOutputFileName();
}