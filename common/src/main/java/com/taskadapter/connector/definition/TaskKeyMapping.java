package com.taskadapter.connector.definition;

import java.util.Objects;

public class TaskKeyMapping {
    public TaskId originalId;
    public TaskId newId;

    public TaskKeyMapping(TaskId originalId, TaskId newId) {
        this.originalId = originalId;
        this.newId = newId;
    }

    public TaskId getOriginalId() {
        return originalId;
    }

    public void setOriginalId(TaskId originalId) {
        this.originalId = originalId;
    }

    public TaskId getNewId() {
        return newId;
    }

    public void setNewId(TaskId newId) {
        this.newId = newId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskKeyMapping that = (TaskKeyMapping) o;
        return Objects.equals(originalId, that.originalId) && Objects.equals(newId, that.newId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalId, newId);
    }

    @Override
    public String toString() {
        return "TaskKeyMapping{" +
                "originalId=" + originalId +
                ", newId=" + newId +
                '}';
    }
}
