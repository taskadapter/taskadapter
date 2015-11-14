package com.taskadapter.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generic "Task" object. Internal representation for all "tasks" loaded from various external systems.
 */
final public class GTask {

    // TODO REVIEW Why not EnumMap?
    private final Map<GTaskDescriptor.FIELD, Object> fields = new HashMap<>();

    public GTask() {
        fields.put(GTaskDescriptor.FIELD.CHILDREN, new ArrayList<GTask>());
        fields.put(GTaskDescriptor.FIELD.RELATIONS, new ArrayList<GRelation>());
    }

    // TODO REVIEW Have you considered a deep-clone method? It is not that hard, but you have
    // to provide some meta-information (cloner) in task field descriptor.
    // Have you considered "shallowClone" (static) method instead of the constructor?
    // Method name would clearly express its function while constructor name does not do that
    // (so it is possible to make subtle mistakes with the constructor but not the method).
    /**
     * Copy-constructor creating a shallow clone.
     */
    public GTask(GTask taskToClone) {
       fields.putAll(taskToClone.fields);
    }

    public Object getValue(GTaskDescriptor.FIELD field) {
        return fields.get(field);
    }

    // TODO REVIEW This method could break getters. task.setValue(FIELD.ID, "'xj").
    //    Have you considered more type-safe field keys? Then this method would be
    //    public <T> void setValue(Field<T> field, T value)
    //    I definitely have shown you this technique (attributes in lpg).
    public void setValue(GTaskDescriptor.FIELD field, Object value) {
        fields.put(field, value);
    }

    /**
     * This is database ID for Redmine and Jira and Unique ID (row number) for MSP.
     */
    public Integer getId() {
        return (Integer) fields.get(GTaskDescriptor.FIELD.ID);
    }

    /**
     * This is database ID for Redmine and Jira and Unique ID (row number) for MSP.
     */
    public void setId(Integer id) {
        fields.put(GTaskDescriptor.FIELD.ID, id);
    }

    public String getParentKey() {
        return (String) fields.get(GTaskDescriptor.FIELD.PARENT_KEY);
    }

    public String getRemoteId() {
        return (String) fields.get(GTaskDescriptor.FIELD.REMOTE_ID);
    }

    public void setRemoteId(String remoteId) {
        fields.put(GTaskDescriptor.FIELD.REMOTE_ID, remoteId);
    }

    public Integer getPriority() {
        return (Integer) fields.get(GTaskDescriptor.FIELD.PRIORITY);
    }

    public void setPriority(Integer priority) {
        fields.put(GTaskDescriptor.FIELD.PRIORITY, priority);
    }

    public GUser getAssignee() {
        return (GUser) fields.get(GTaskDescriptor.FIELD.ASSIGNEE);
    }

    public void setAssignee(GUser assignee) {
        fields.put(GTaskDescriptor.FIELD.ASSIGNEE, assignee);
    }

    public String getSummary() {
        return (String) fields.get(GTaskDescriptor.FIELD.SUMMARY);
    }

    public void setSummary(String summary) {
        fields.put(GTaskDescriptor.FIELD.SUMMARY, summary);
    }

    public String getDescription() {
        return (String) fields.get(GTaskDescriptor.FIELD.DESCRIPTION);
    }

    public void setDescription(String description) {
        fields.put(GTaskDescriptor.FIELD.DESCRIPTION, description);
    }

    public Float getEstimatedHours() {
        return (Float) fields.get(GTaskDescriptor.FIELD.ESTIMATED_TIME);
    }

    public void setEstimatedHours(Float estimatedHours) {
        fields.put(GTaskDescriptor.FIELD.ESTIMATED_TIME, estimatedHours);
    }

    /**
     * @return %% complete (e.g. "30%")
     */
    public Integer getDoneRatio()  {
        return (Integer) fields.get(GTaskDescriptor.FIELD.DONE_RATIO);
    }

    public void setDoneRatio(Integer doneRatio) {
        fields.put(GTaskDescriptor.FIELD.DONE_RATIO, doneRatio);
    }

    public Date getStartDate() {
        return (Date) fields.get(GTaskDescriptor.FIELD.START_DATE);
    }

    public void setStartDate(Date startDate) {
        fields.put(GTaskDescriptor.FIELD.START_DATE, startDate);
    }

    public Date getDueDate() {
        return (Date) fields.get(GTaskDescriptor.FIELD.DUE_DATE);
    }

    public void setDueDate(Date dueDate) {
        fields.put(GTaskDescriptor.FIELD.DUE_DATE, dueDate);
    }

    /**
     *
     * @return the list of children of an empty list when no children. never NULL.
     */
    public List<GTask> getChildren() {
        return (List<GTask>) fields.get(GTaskDescriptor.FIELD.CHILDREN);
    }

    public void setChildren(List<GTask> children) {
        fields.put(GTaskDescriptor.FIELD.CHILDREN, children);
    }

    public boolean hasChildren() {
        return getChildren() != null && !getChildren().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GTask gTask = (GTask) o;

        if (fields != null ? !fields.equals(gTask.fields) : gTask.fields != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = fields != null ? fields.hashCode() : 0;
        return result;
    }

    public String getType() {
        return (String) fields.get(GTaskDescriptor.FIELD.TASK_TYPE);
    }

    public void setType(String type) {
        fields.put(GTaskDescriptor.FIELD.TASK_TYPE, type);
    }

    public String getStatus() {
        return (String) fields.get(GTaskDescriptor.FIELD.TASK_STATUS);
    }

    public void setStatus(String status) {
        fields.put(GTaskDescriptor.FIELD.TASK_STATUS, status);
    }

    /**
     * Some systems like Jira can have string-based "key" like "TEST-1"
     * to identify issues. This is NOT a database identifier.
     */
    public String getKey() {
        return (String) fields.get(GTaskDescriptor.FIELD.KEY);
    }

    /**
     * Some systems like Jira can have string-based "key" like "TEST-1"
     * to identify issues. This is NOT a database identifier.
     */
    public void setKey(String key) {
        fields.put(GTaskDescriptor.FIELD.KEY, key);
    }

    public Date getCreatedOn() {
        return (Date) fields.get(GTaskDescriptor.FIELD.CREATED_ON);
    }

    public void setCreatedOn(Date createdOn) {
        fields.put(GTaskDescriptor.FIELD.CREATED_ON, createdOn);
    }

    public Date getUpdatedOn() {
        return (Date) fields.get(GTaskDescriptor.FIELD.UPDATED_ON);
    }

    public void setUpdatedOn(Date updatedOn) {
        fields.put(GTaskDescriptor.FIELD.UPDATED_ON, updatedOn);
    }
    
    public Date getClosedDate() {
        return (Date) fields.get(GTaskDescriptor.FIELD.CLOSE_DATE);
    }

    public void setClosedOn(Date closedOn) {
        fields.put(GTaskDescriptor.FIELD.CLOSE_DATE, closedOn);
    }

    public List<GRelation> getRelations() {
        return (List<GRelation>) fields.get(GTaskDescriptor.FIELD.RELATIONS);
    }

    public void setRelations(List<GRelation> relations) {
        fields.put(GTaskDescriptor.FIELD.RELATIONS, relations);
    }

    @Override
    public String toString() {
        return "GTask [id=" + getId() + ", key=" + getKey() + ", remoteId=" + getRemoteId()
                + ", summary=" + getSummary() + "]";
    }

    public void setParentKey(String parentIssueKey) {
        fields.put(GTaskDescriptor.FIELD.PARENT_KEY, parentIssueKey);
    }

    public String getEnvironment() {
        return (String) fields.get(GTaskDescriptor.FIELD.ENVIRONMENT);
    }

    public void setEnvironment(String environment) {
        fields.put(GTaskDescriptor.FIELD.ENVIRONMENT, environment);
    }

    public String getTargetVersionName() {
        return (String) fields.get(GTaskDescriptor.FIELD.TARGET_VERSION);
    }

    /**
     * @param versionName like "release 1.0"
     */
    public void setTargetVersionName(String versionName) {
        fields.put(GTaskDescriptor.FIELD.TARGET_VERSION, versionName);
    }

}
