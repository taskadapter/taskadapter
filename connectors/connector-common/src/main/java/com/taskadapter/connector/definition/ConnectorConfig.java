package com.taskadapter.connector.definition;

import com.google.common.base.Objects;
import com.taskadapter.connector.Priorities;
import com.taskadapter.model.GTaskDescriptor;
import com.taskadapter.model.GTaskDescriptor.FIELD;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Alexey Skorokhodov
 */
public abstract class ConnectorConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    protected Map<FIELD, Mapping> fieldsMapping;

    private String label;

    protected boolean saveIssueRelations = false;

    /**
     * Samples: "Bug", "Task", "Feature", "Support"
     */
    protected String defaultTaskType;

    protected Priorities priorities;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Label describing where the data is located.
     * <p>sample values:
     * for Redmine connector: "http://www.redmine.org:1234",
     * for MSProject: "c:\folder1\file.txt"
     */
    public abstract String getSourceLocation();

    public abstract String getTargetLocation();

    public ConnectorConfig(String label) {
        this.label = label;
        fieldsMapping = generateDefaultFieldsMapping();
        priorities = generateDefaultPriorities();
    }

    public boolean getSaveIssueRelations() {
        return saveIssueRelations;
    }

    public void setSaveIssueRelations(boolean saveIssueRelations) {
        this.saveIssueRelations = saveIssueRelations;
    }

    /**
      TODO replace the MAP with a concrete class holding the mapping
     */
    @Deprecated
    public Map<FIELD, Mapping> getFieldsMapping() {
        return fieldsMapping;
    }

    public void setFieldsMapping(Map<FIELD, Mapping> fieldsMapping) {
        this.fieldsMapping = fieldsMapping;
    }

    abstract protected Map<GTaskDescriptor.FIELD, Mapping> generateDefaultFieldsMapping();

    abstract protected Priorities generateDefaultPriorities();

    public boolean isFieldSelected(FIELD field) {
        Mapping mapping = fieldsMapping.get(field);
        return (mapping != null && mapping.isSelected());
    }

    /**
     * returns the current value or NULL if the mapping does not exist / unknown
     */
    public String getFieldMappedValue(FIELD field) {
        Mapping mapping = fieldsMapping.get(field);
        if (mapping != null) {
            return mapping.getCurrentValue();
        }
        return null;
    }

    /**
     * Deprecated: ise getFieldMappedValue(FIELD) instead
     */
    @Deprecated
    public Mapping getFieldMapping(FIELD field) {
        return fieldsMapping.get(field);
    }

    public void setFieldMapping(FIELD field, Mapping mapping) {
        fieldsMapping.put(field, mapping);
    }

    public String getDefaultTaskType() {
        return defaultTaskType;
    }

    public void setDefaultTaskType(String defaultTaskType) {
        this.defaultTaskType = defaultTaskType;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(fieldsMapping, defaultTaskType, label);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ConnectorConfig) {
            ConnectorConfig other = (ConnectorConfig) obj;
            return Objects.equal(fieldsMapping, other.fieldsMapping) &&
                    Objects.equal(defaultTaskType, other.defaultTaskType) &&
                    Objects.equal(label, other.label);
        } else {
            return false;
        }
    }

    /**
     * The default implementation does nothing.
     */
    public void validateForSave() throws ValidationException {
        // nothing
    }

    /**
     * The default implementation does nothing.
     */
    public void validateForLoad() throws ValidationException {
        // nothing
    }

    public String getPriorityByMSP(Integer priority) {
        return priorities.getPriorityByMSP(priority);
    }

    public Integer getPriorityByText(String priorityName) {
        return priorities.getPriorityByText(priorityName);
    }

    public void setPriorities(Priorities priorities) {
        this.priorities = priorities;
    }

    public Priorities getPriorities() {
        return priorities;
    }
}
