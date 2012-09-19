package com.taskadapter.connector.definition;

/**
 * All Task Adapter Data Connectors must implement this interface.
 *
 * TODO: Get rid of "implementation" interfaces, use a "plain data" class.
 * Maybe use properties for this task?
 */
public final class Descriptor {

    private final String id;

    private final String label;

    /**
     * Supported (available) fields.
     */
    private final AvailableFields fields;
    
    public Descriptor(String id, String label,
            AvailableFields fields) {
        this.id = id;
        this.label = label;
        this.fields = fields;
    }

    /**
     * get the Connector ID. Once defined, the ID should not be changed in the connectors to avoid breaking compatibility.
     */
    public String getID() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public AvailableFields getAvailableFields() {
        return fields;
    }
}
