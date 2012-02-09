package com.taskadapter.model;

public class GRelation {
    public static enum TYPE {
        // XXX this is used directly, need to use type-safe!!!
        precedes
    }

    private String taskKey;
    private String relatedTaskKey;
    private TYPE type;
    private Integer delay;

    public GRelation(String taskKey, String relatedTaskKey, TYPE type) {
        super();
        this.taskKey = taskKey;
        this.relatedTaskKey = relatedTaskKey;
        this.type = type;
    }

    public String getRelatedTaskKey() {
        return relatedTaskKey;
    }

    public TYPE getType() {
        return type;
    }

    public String getTaskKey() {
        return taskKey;
    }

    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    @Override
    public String toString() {
        return "GRelation [taskKey=" + taskKey + ", relatedTaskKey="
                + relatedTaskKey + ", type=" + type + ", delay=" + delay + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((delay == null) ? 0 : delay.hashCode());
        result = prime * result
                + ((relatedTaskKey == null) ? 0 : relatedTaskKey.hashCode());
        result = prime * result + ((taskKey == null) ? 0 : taskKey.hashCode());
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
        if (relatedTaskKey == null) {
            if (other.relatedTaskKey != null) {
                return false;
            }
        } else if (!relatedTaskKey.equals(other.relatedTaskKey)) {
            return false;
        }
        if (taskKey == null) {
            if (other.taskKey != null) {
                return false;
            }
        } else if (!taskKey.equals(other.taskKey)) {
            return false;
        }
        return type == other.type;
    }


}
