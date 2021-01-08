package com.taskadapter.web.uiapi;

import java.util.Objects;

public class Schedule {
    private String id;
    private ConfigId configId;
    private int intervalInMinutes;
    private boolean directionLeft;
    private boolean directionRight;

    public Schedule(String id, ConfigId configId, int intervalInMinutes, boolean directionLeft, boolean directionRight) {
        this.id = id;
        this.configId = configId;
        this.intervalInMinutes = intervalInMinutes;
        this.directionLeft = directionLeft;
        this.directionRight = directionRight;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ConfigId getConfigId() {
        return configId;
    }

    public void setConfigId(ConfigId configId) {
        this.configId = configId;
    }

    public int getIntervalInMinutes() {
        return intervalInMinutes;
    }

    public void setIntervalInMinutes(int intervalInMinutes) {
        this.intervalInMinutes = intervalInMinutes;
    }

    public boolean isDirectionLeft() {
        return directionLeft;
    }

    public void setDirectionLeft(boolean directionLeft) {
        this.directionLeft = directionLeft;
    }

    public boolean isDirectionRight() {
        return directionRight;
    }

    public void setDirectionRight(boolean directionRight) {
        this.directionRight = directionRight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Schedule schedule = (Schedule) o;
        return intervalInMinutes == schedule.intervalInMinutes &&
                directionLeft == schedule.directionLeft &&
                directionRight == schedule.directionRight &&
                Objects.equals(id, schedule.id) &&
                Objects.equals(configId, schedule.configId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, configId, intervalInMinutes, directionLeft, directionRight);
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "id='" + id + '\'' +
                ", configId=" + configId +
                ", intervalInMinutes=" + intervalInMinutes +
                ", directionLeft=" + directionLeft +
                ", directionRight=" + directionRight +
                '}';
    }
}
