package com.taskadapter.core;

import java.util.Objects;

public class TaskKeeperLocation {
    private String location1;
    private String location2;
    private String cacheFileLocation;

    /**
     * an empty constructor is required for Google GSon before version 1.7.
     */
    public TaskKeeperLocation() {
    }

    public TaskKeeperLocation(String location1, String location2, String cacheFileLocation) {
        this.location1 = location1;
        this.location2 = location2;
        this.cacheFileLocation = cacheFileLocation;
    }

    public String getLocation1() {
        return location1;
    }

    public void setLocation1(String location1) {
        this.location1 = location1;
    }

    public String getLocation2() {
        return location2;
    }

    public void setLocation2(String location2) {
        this.location2 = location2;
    }

    public String getCacheFileLocation() {
        return cacheFileLocation;
    }

    public void setCacheFileLocation(String cacheFileLocation) {
        this.cacheFileLocation = cacheFileLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskKeeperLocation that = (TaskKeeperLocation) o;
        return Objects.equals(location1, that.location1) && Objects.equals(location2, that.location2) && Objects.equals(cacheFileLocation, that.cacheFileLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location1, location2, cacheFileLocation);
    }

    @Override
    public String toString() {
        return "TaskKeeperLocation{" +
                "location1='" + location1 + '\'' +
                ", location2='" + location2 + '\'' +
                ", cacheFileLocation='" + cacheFileLocation + '\'' +
                '}';
    }
}
