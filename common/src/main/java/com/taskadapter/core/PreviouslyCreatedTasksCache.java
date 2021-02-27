package com.taskadapter.core;

import com.taskadapter.connector.definition.TaskKeyMapping;

import java.util.List;
import java.util.Objects;

public class PreviouslyCreatedTasksCache {
    private String location1;
    private String location2;
    private List<TaskKeyMapping> items;

    public PreviouslyCreatedTasksCache() {
    }

    public PreviouslyCreatedTasksCache(String location1, String location2, List<TaskKeyMapping> items) {
        this.location1 = location1;
        this.location2 = location2;
        this.items = items;
    }

    public String getLocation1() {
        return location1;
    }

    public PreviouslyCreatedTasksCache setLocation1(String location1) {
        this.location1 = location1;
        return this;
    }

    public String getLocation2() {
        return location2;
    }

    public PreviouslyCreatedTasksCache setLocation2(String location2) {
        this.location2 = location2;
        return this;
    }

    public List<TaskKeyMapping> getItems() {
        return items;
    }

    public PreviouslyCreatedTasksCache setItems(List<TaskKeyMapping> items) {
        this.items = items;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PreviouslyCreatedTasksCache that = (PreviouslyCreatedTasksCache) o;
        return Objects.equals(location1, that.location1) && Objects.equals(location2, that.location2) && Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location1, location2, items);
    }

    @Override
    public String toString() {
        return "PreviouslyCreatedTasksCache{" +
                "location1='" + location1 + '\'' +
                ", location2='" + location2 + '\'' +
                ", items=" + items +
                '}';
    }
}
