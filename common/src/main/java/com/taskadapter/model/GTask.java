package com.taskadapter.model;

import com.taskadapter.connector.Field;
import com.taskadapter.connector.definition.TaskId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generic "Task" object. Internal representation for all "tasks" loaded from various external systems.
 */
final public class GTask {

    // TODO REVIEW Why not EnumMap?
    private final Map<String, Object> fields = new HashMap<>();

    public GTask() {
        setValue(GTaskDescriptor.FIELD.CHILDREN, new ArrayList<GTask>());
        setValue(GTaskDescriptor.FIELD.RELATIONS, new ArrayList<GRelation>());
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
        return getValue(field.name());
    }

    public Object getValue(Field field) {
        return getValue(field.name());
    }

    public Object getValue(String field) {
        return fields.get(field);
    }

    // TODO REVIEW This method could break getters. task.setValue(FIELD.ID, "'xj").
    //    Have you considered more type-safe field keys? Then this method would be
    //    public <T> void setValue(Field<T> field, T value)
    //    I definitely have shown you this technique (attributes in lpg).
    public void setValue(GTaskDescriptor.FIELD field, Object value) {
        fields.put(field.name(), value);
    }

    public void setValue(Field field, Object value) {
        fields.put(field.name(), value);
    }
    public void setValue(String field, Object value) {
        fields.put(field, value);
    }

    public TaskId getIdentity() {
        Long id = getId();
        if (id == null) {
            id = 0L;
        }
        return new TaskId(id, getKey());
    }

    /**
     * Like database ID for Redmine and Jira and Unique ID (row number) for MSP.
     */
    public Long getId() {
        return (Long) getValue(GTaskDescriptor.FIELD.ID);
    }

    /**
     * This is database ID for Redmine and Jira and Unique ID (row number) for MSP.
     */
    public void setId(Long id) {
        setValue(GTaskDescriptor.FIELD.ID, id);
    }

    public TaskId getParentIdentity() {
        return (TaskId) getValue(GTaskDescriptor.FIELD.PARENT_KEY);
    }

    public TaskId getSourceSystemId() {
        return (TaskId) getValue(GTaskDescriptor.FIELD.SOURCE_SYSTEM_ID);
    }

    public void setSourceSystemId(TaskId sourceSystemId) {
        setValue(GTaskDescriptor.FIELD.SOURCE_SYSTEM_ID, sourceSystemId);
    }

    /**
     *
     * @return the list of children of an empty list when no children. never NULL.
     */
    public List<GTask> getChildren() {
        return (List<GTask>) getValue(GTaskDescriptor.FIELD.CHILDREN);
    }

    public void addChildTask(GTask child) {
        getChildren().add(child);
    }
    public void setChildren(List<GTask> children) {
        setValue(GTaskDescriptor.FIELD.CHILDREN, children);
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

    /**
     * Some systems like Jira can have string-based "key" like "TEST-1"
     * to identify issues. This is NOT a database identifier.
     */
    public String getKey() {
        return (String) getValue(GTaskDescriptor.FIELD.KEY);
    }

    /**
     * Some systems like Jira can have string-based "key" like "TEST-1"
     * to identify issues. This is NOT a database identifier.
     */
    public void setKey(String key) {
        setValue(GTaskDescriptor.FIELD.KEY, key);
    }

    public List<GRelation> getRelations() {
        return (List<GRelation>) getValue(GTaskDescriptor.FIELD.RELATIONS);
    }

    public void setRelations(List<GRelation> relations) {
        setValue(GTaskDescriptor.FIELD.RELATIONS, relations);
    }

    @Override
    public String toString() {
        return fields.toString();
    }

    public void setParentIdentity(TaskId parentIssueKey) {
        setValue(GTaskDescriptor.FIELD.PARENT_KEY, parentIssueKey);
    }

    public Map<String, Object> getFields() {
        return fields;
    }
}
