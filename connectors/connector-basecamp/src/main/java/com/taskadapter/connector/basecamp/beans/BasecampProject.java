package com.taskadapter.connector.basecamp.beans;

public class BasecampProject {
    private String key;
    private String name;
    private String description;
    private int completedTodolists;
    private int remainingTodolists;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCompletedTodolists() {
        return completedTodolists;
    }

    public void setCompletedTodolists(int completedTodolists) {
        this.completedTodolists = completedTodolists;
    }

    public int getRemainingTodolists() {
        return remainingTodolists;
    }

    public void setRemainingTodolists(int remainingTodolists) {
        this.remainingTodolists = remainingTodolists;
    }
}
