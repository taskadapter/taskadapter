package com.taskadapter.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Generic "Task" object. Internal representation for all "tasks" loaded from various external systems.
 *
 * @author Alexey Skorokhodov
 */
public class GTask {

    /**
     * This is database ID for Redmine and Jira and Unique ID (row number) for MSP.
     */
    private Integer id;

    /**
     * Some systems like Jira can have string-based "key" like "TEST-1"
     * to identify issues. This is NOT a database identifier.
     */
    private String key;
    private String parentKey;
    private String remoteId;
    private Integer priority;
    private GUser assignee;
    private String summary;
    private String description;
    private Float estimatedHours;
    private Integer doneRatio;
    private Date startDate;
    private Date dueDate;
    private String type;
    private String status;
    private List<GTask> children = new ArrayList<GTask>();
    private Date createdOn;
    private Date updatedOn;
    private List<GRelation> relations = new ArrayList<GRelation>();

    /**
     * this constructor does NOT copy children!
     */
    public GTask(GTask taskFromItem) {
        this.id = taskFromItem.getId();
        this.key = taskFromItem.getKey();
        this.parentKey = taskFromItem.getParentKey();
        this.remoteId = taskFromItem.getRemoteId();
        this.priority = taskFromItem.getPriority();
        this.assignee = taskFromItem.getAssignee();
        this.summary = taskFromItem.getSummary();
        this.description = taskFromItem.getDescription();
        this.estimatedHours = taskFromItem.getEstimatedHours();
        this.doneRatio = taskFromItem.getDoneRatio();
        this.startDate = taskFromItem.getStartDate();
        this.dueDate = taskFromItem.getDueDate();
        this.type = taskFromItem.getType();
        this.status = taskFromItem.getStatus();
        this.createdOn = taskFromItem.getCreatedOn();
        this.updatedOn = taskFromItem.getUpdatedOn();
        this.relations = taskFromItem.getRelations();
    }

    public GTask() {
    }

    public GTask(Integer id) {
        this.id = id;
    }

    public GTask(Integer id, List<GTask> children) {
        this.id = id;
        this.children = children;
    }

    /**
     * This is database ID for Redmine and Jira and Unique ID (row number) for MSP.
     */
    public Integer getId() {
        return id;
    }

    /**
     * This is database ID for Redmine and Jira and Unique ID (row number) for MSP.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    public String getParentKey() {
        return parentKey;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public GUser getAssignee() {
        return assignee;
    }

    public void setAssignee(GUser assignee) {
        this.assignee = assignee;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(Float estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    /**
     * @return %% complete (e.g. "30%")
     */
    public Integer getDoneRatio() {
        return doneRatio;
    }

    public void setDoneRatio(Integer doneRatio) {
        this.doneRatio = doneRatio;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    /**
     *
     * @return the list of children of an empty list when no children. never NULL.
     */
    public List<GTask> getChildren() {
        return children;
    }

    public void setChildren(List<GTask> children) {
        this.children = children;
    }

    public boolean hasChildren() {
        return children == null || !children.isEmpty();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((assignee == null) ? 0 : assignee.hashCode());
        result = prime * result
                + ((children == null) ? 0 : children.hashCode());
        result = prime * result
                + ((createdOn == null) ? 0 : createdOn.hashCode());
        result = prime * result
                + ((description == null) ? 0 : description.hashCode());
        result = prime * result
                + ((doneRatio == null) ? 0 : doneRatio.hashCode());
        result = prime * result + ((dueDate == null) ? 0 : dueDate.hashCode());
        result = prime * result
                + ((estimatedHours == null) ? 0 : estimatedHours.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result
                + ((parentKey == null) ? 0 : parentKey.hashCode());
        result = prime * result
                + ((priority == null) ? 0 : priority.hashCode());
        result = prime * result
                + ((relations == null) ? 0 : relations.hashCode());
        result = prime * result
                + ((remoteId == null) ? 0 : remoteId.hashCode());
        result = prime * result
                + ((startDate == null) ? 0 : startDate.hashCode());
        result = prime * result + ((summary == null) ? 0 : summary.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result
                + ((updatedOn == null) ? 0 : updatedOn.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GTask other = (GTask) obj;
        if (assignee == null) {
            if (other.assignee != null)
                return false;
        } else if (!assignee.equals(other.assignee))
            return false;
        if (children == null) {
            if (other.children != null)
                return false;
        } else if (!children.equals(other.children))
            return false;
        if (createdOn == null) {
            if (other.createdOn != null)
                return false;
        } else if (!createdOn.equals(other.createdOn))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (doneRatio == null) {
            if (other.doneRatio != null)
                return false;
        } else if (!doneRatio.equals(other.doneRatio))
            return false;
        if (dueDate == null) {
            if (other.dueDate != null)
                return false;
        } else if (!dueDate.equals(other.dueDate))
            return false;
        if (estimatedHours == null) {
            if (other.estimatedHours != null)
                return false;
        } else if (!estimatedHours.equals(other.estimatedHours))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        if (parentKey == null) {
            if (other.parentKey != null)
                return false;
        } else if (!parentKey.equals(other.parentKey))
            return false;
        if (priority == null) {
            if (other.priority != null)
                return false;
        } else if (!priority.equals(other.priority))
            return false;
        if (relations == null) {
            if (other.relations != null)
                return false;
        } else if (!relations.equals(other.relations))
            return false;
        if (remoteId == null) {
            if (other.remoteId != null)
                return false;
        } else if (!remoteId.equals(other.remoteId))
            return false;
        if (startDate == null) {
            if (other.startDate != null)
                return false;
        } else if (!startDate.equals(other.startDate))
            return false;
        if (summary == null) {
            if (other.summary != null)
                return false;
        } else if (!summary.equals(other.summary))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;

        if (status == null) {
            if (other.status != null)
                return false;
        } else if (!status.equals(other.status))
            return false;

        if (updatedOn == null) {
            if (other.updatedOn != null)
                return false;
        } else if (!updatedOn.equals(other.updatedOn))
            return false;
        return true;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Some systems like Jira can have string-based "key" like "TEST-1"
     * to identify issues. This is NOT a database identifier.
     */
    public String getKey() {
        return key;
    }

    /**
     * Some systems like Jira can have string-based "key" like "TEST-1"
     * to identify issues. This is NOT a database identifier.
     */
    public void setKey(String key) {
        this.key = key;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    public List<GRelation> getRelations() {
        return relations;
    }

    @Override
    public String toString() {
        return "GTask [id=" + id + ", key=" + key + ", remoteId=" + remoteId
                + ", summary=" + summary + "]";
    }

    public void setParentKey(String parentIssueKey) {
        parentKey = parentIssueKey;
    }
}
