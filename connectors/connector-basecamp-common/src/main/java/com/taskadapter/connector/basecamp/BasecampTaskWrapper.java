package com.taskadapter.connector.basecamp;

import java.util.Objects;

public class BasecampTaskWrapper {
    private String key;
    private String nativeTask;
    private Float doneRatio;

    public BasecampTaskWrapper(String key, String nativeTask, Float doneRatio) {
        this.key = key;
        this.nativeTask = nativeTask;
        this.doneRatio = doneRatio;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNativeTask() {
        return nativeTask;
    }

    public void setNativeTask(String nativeTask) {
        this.nativeTask = nativeTask;
    }

    public Float getDoneRatio() {
        return doneRatio;
    }

    public void setDoneRatio(Float doneRatio) {
        this.doneRatio = doneRatio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasecampTaskWrapper that = (BasecampTaskWrapper) o;
        return Objects.equals(key, that.key) && Objects.equals(nativeTask, that.nativeTask) && Objects.equals(doneRatio, that.doneRatio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, nativeTask, doneRatio);
    }
}
