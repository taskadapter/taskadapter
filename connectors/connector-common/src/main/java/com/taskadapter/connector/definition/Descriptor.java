package com.taskadapter.connector.definition;

public final class Descriptor {

    private final String id;

    private final String label;

    public Descriptor(String id, String label) {
        this.id = id;
        this.label = label;
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
}
