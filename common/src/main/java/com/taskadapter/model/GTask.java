package com.taskadapter.model;

import com.taskadapter.connector.definition.TaskId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generic "Task" object. Internal representation for all "tasks" loaded from various external systems.
 */
public class GTask {
    /**
     * map: field -> field value
     */
    private final Map<Field<?>, Object> fields = new HashMap<>();

    public GTask() {
        setChildren(new ArrayList<>());
        setRelations(new ArrayList<>());
    }

    public static GTask shallowClone(GTask taskToClone) {
        var task = new GTask();
        task.fields.putAll(taskToClone.fields);
        return task;
    }

    /**
     * database ID for Redmine and Jira and Unique ID (row number) for MSP.
     */
    public Long getId() {
        return getValue(AllFields.id);
    }

    /**
     * This is database ID for Redmine and Jira and Unique ID (row number) for MSP.
     */
    public GTask setId(Long id) {
        setValue(AllFields.id, id);
        return this;
    }

    /**
     * Some systems like Jira can have string-based "key" like "TEST-1"
     * to identify issues. This is NOT a database identifier.
     */
    public String getKey() {
        return getValue(AllFields.key);
    }

    public GTask setKey(String key) {
        setValue(AllFields.key, key);
        return this;
    }

    public TaskId getIdentity() {
        var id = getId();
        if (id == null) {
            id = 0L;
        }
        return new TaskId(id, getKey());
    }


    public <T> T getValue(Field<T> field) {
        return (T) fields.get(field);
    }

    public <T> GTask setValue(Field<T> field, T value) {
        fields.put(field, value);
        return this;
    }

    public GTask setChildren(List<GTask> children) {
        setValue(AllFields.children, children);
        return this;
    }

    private GTask setRelations(List<GRelation> relations) {
        setValue(AllFields.relations, relations);
        return this;
    }

    /**
     * @return the list of children of an empty list when no children. never NULL.
     */
    public List<GTask> getChildren() {
        return getValue(AllFields.children);
    }

    public List<GRelation> getRelations() {
        return getValue(AllFields.relations);
    }

    public TaskId getParentIdentity() {
        return getValue(AllFields.parentKey);
    }

    public TaskId getSourceSystemId() {
        return getValue(AllFields.sourceSystemId);
    }

    public GTask setSourceSystemId(TaskId sourceSystemId) {
        setValue(AllFields.sourceSystemId, sourceSystemId);
        return this;
    }

    public void addChildTask(GTask child) {
        getChildren().add(child);
    }

    public boolean hasChildren() {
        return getChildren() != null && !getChildren().isEmpty();
    }

    public GTask setParentIdentity(TaskId parentIssueKey) {
        setValue(AllFields.parentKey, parentIssueKey);
        return this;
    }

    public Map<Field<?>, Object> getFields() {
        return fields;
    }
}
