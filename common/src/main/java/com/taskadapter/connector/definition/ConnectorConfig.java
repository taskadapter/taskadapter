package com.taskadapter.connector.definition;

import com.google.common.base.Objects;
import com.taskadapter.connector.Priorities;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ConnectorConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Priorities priorities;

    private String label;

    private boolean saveIssueRelations = false;

    /**
     * Samples: "Bug", "Task", "Feature", "Support"
     */
    private String defaultTaskType;

    /**
     * Creates a new connector config. This constructor creates a new instance
     * of priorities to be used inside this class. Such behavior allows to
     * connectors to have single constant value for a priorities.
     * 
     * @param defaultPriorities
     *            default priorities.
     */
    public ConnectorConfig(Priorities defaultPriorities) {
        priorities = new Priorities(defaultPriorities);
    }

    /**
     * Creates a new connector config. This constructor creates a new instance
     * of priorities to be used inside this class. Such behavior allows to
     * connectors to have single constant value for a priorities.
     * 
     * @param defaultPriorities
     *            default priorities.
     */
    public ConnectorConfig(Map<String, Integer> defaultPriorities) {
        priorities = new Priorities(new HashMap<>(defaultPriorities));
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

    public Priorities getPriorities() {
        return priorities;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
