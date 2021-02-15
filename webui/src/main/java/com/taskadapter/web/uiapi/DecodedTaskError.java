package com.taskadapter.web.uiapi;

import com.taskadapter.connector.definition.TaskId;

import java.util.Objects;

public class DecodedTaskError {
    public final TaskId sourceSystemTaskId;
    public final String connector2ErrorText;
    public final  String exceptionStackTrace;

    public DecodedTaskError(TaskId sourceSystemTaskId, String connector2ErrorText, String exceptionStackTrace) {
        this.sourceSystemTaskId = sourceSystemTaskId;
        this.connector2ErrorText = connector2ErrorText;
        this.exceptionStackTrace = exceptionStackTrace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DecodedTaskError that = (DecodedTaskError) o;
        return Objects.equals(sourceSystemTaskId, that.sourceSystemTaskId) && Objects.equals(connector2ErrorText, that.connector2ErrorText) && Objects.equals(exceptionStackTrace, that.exceptionStackTrace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceSystemTaskId, connector2ErrorText, exceptionStackTrace);
    }
}
