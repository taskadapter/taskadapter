package com.taskadapter.connector.definition;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GTask;

import java.util.List;

public interface FileBasedConnector {
    void updateTasksByRemoteIds(List<GTask> tasks, Iterable<FieldRow> rows) throws ConnectorException;

    boolean fileExists();

    // TODO use it. see how it's done in Eclipse branch
    void validateCanUpdate() throws BadConfigException;

    String getAbsoluteOutputFileName();
}