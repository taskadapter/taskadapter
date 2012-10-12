package com.taskadapter.connector.definition;

import com.google.common.base.Objects;
import com.taskadapter.connector.Priorities;

import java.io.Serializable;

public abstract class ConnectorConfig implements Serializable {

    private static final long serialVersionUID = 1L;

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
        priorities = generateDefaultPriorities();
    }

    public ConnectorConfig(ConnectorConfig configToDeepClone) {
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

    abstract protected Priorities generateDefaultPriorities();

    public String getDefaultTaskType() {
        return defaultTaskType;
    }

    public void setDefaultTaskType(String defaultTaskType) {
        this.defaultTaskType = defaultTaskType;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(defaultTaskType, label);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ConnectorConfig) {
            ConnectorConfig other = (ConnectorConfig) obj;
            return Objects.equal(defaultTaskType, other.defaultTaskType) &&
                    Objects.equal(label, other.label);
        } else {
            return false;
        }
    }

    public void setPriorities(Priorities priorities) {
        this.priorities = priorities;
    }

    public Priorities getPriorities() {
        return priorities;
    }
}
