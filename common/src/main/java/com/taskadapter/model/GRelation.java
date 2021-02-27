package com.taskadapter.model;

import com.taskadapter.connector.definition.TaskId;

public class GRelation {
    private TaskId taskId;
    private TaskId relatedTaskId;
    private GRelationType type;
    private Integer delay;

    public GRelation(TaskId taskId, TaskId relatedTaskId, GRelationType type) {
        super();
        this.taskId = taskId;
        this.relatedTaskId = relatedTaskId;
        this.type = type;
    }

    public TaskId getRelatedTaskId() {
        return relatedTaskId;
    }

    public GRelationType getType() {
        return type;
    }

    public TaskId getTaskId() {
        return taskId;
    }

    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    @Override
    public String toString() {
        return "GRelation [taskKey=" + taskId + ", relatedTaskKey="
                + relatedTaskId + ", type=" + type + ", delay=" + delay + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((delay == null) ? 0 : delay.hashCode());
        result = prime * result
                + ((relatedTaskId == null) ? 0 : relatedTaskId.hashCode());
        result = prime * result + ((taskId == null) ? 0 : taskId.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        GRelation other = (GRelation) obj;
        if (delay == null) {
            if (other.delay != null) {
                return false;
            }
        } else if (!delay.equals(other.delay)) {
            return false;
        }
        if (relatedTaskId == null) {
            if (other.relatedTaskId != null) {
                return false;
            }
        } else if (!relatedTaskId.equals(other.relatedTaskId)) {
            return false;
        }
        if (taskId == null) {
            if (other.taskId != null) {
                return false;
            }
        } else if (!taskId.equals(other.taskId)) {
            return false;
        }
        return type == other.type;
    }


}
