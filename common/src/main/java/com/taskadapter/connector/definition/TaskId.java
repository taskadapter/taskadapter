package com.taskadapter.connector.definition;

import java.util.Objects;

public class TaskId {
    private Long id;
    private String key;

    public TaskId(Long id, String key) {
        this.id = id;
        this.key = key;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskId taskId = (TaskId) o;
        return Objects.equals(id, taskId.id) && Objects.equals(key, taskId.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, key);
    }

    @Override
    public String toString() {
        return "TaskId{" +
                "id=" + id +
                ", key='" + key + '\'' +
                '}';
    }
}
