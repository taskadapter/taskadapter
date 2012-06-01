package com.taskadapter.connector.definition;

import com.google.common.base.Objects;
import com.taskadapter.connector.Priorities;
import com.taskadapter.model.GTaskDescriptor;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Alexey Skorokhodov
 */
public abstract class ConnectorConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Mappings for a connector.
     */
    private Mappings mappings;

    private String label;

    private boolean saveIssueRelations = false;

    /**
     * Samples: "Bug", "Task", "Feature", "Support"
     */
    private String defaultTaskType;

    private Priorities priorities;

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

    public ConnectorConfig() {
        mappings = new Mappings(generateDefaultFieldsMapping());
        priorities = generateDefaultPriorities();
    }

    public ConnectorConfig(ConnectorConfig configToDeepClone) {
        this.mappings = new Mappings(configToDeepClone.mappings);

        saveIssueRelations = configToDeepClone.getSaveIssueRelations();
        defaultTaskType = configToDeepClone.getDefaultTaskType();
        this.priorities = new Priorities(configToDeepClone.getPriorities());
    }

    public boolean getSaveIssueRelations() {
        return saveIssueRelations;
    }

    public void setSaveIssueRelations(boolean saveIssueRelations) {
        this.saveIssueRelations = saveIssueRelations;
    }

    abstract protected Map<GTaskDescriptor.FIELD, Mapping> generateDefaultFieldsMapping();

    abstract protected Priorities generateDefaultPriorities();

    /**
     * Returns fields mappings.
     * @return fields mappings.
     */
    public Mappings getFieldMappings() {
    	return mappings;
    }

    public String getDefaultTaskType() {
        return defaultTaskType;
    }

    public void setDefaultTaskType(String defaultTaskType) {
        this.defaultTaskType = defaultTaskType;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mappings, defaultTaskType, label);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ConnectorConfig) {
            ConnectorConfig other = (ConnectorConfig) obj;
            return Objects.equal(mappings, other.mappings) &&
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
