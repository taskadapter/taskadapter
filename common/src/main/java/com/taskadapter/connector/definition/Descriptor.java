package com.taskadapter.connector.definition;

public final class Descriptor {

    private final String id;
    private final String label;

    /**
     * @param id    the Connector id. Once defined in a connector, this id should not be changed in the connector
     *              to avoid breaking compatibility.
     * @param label user-friendly label to show on "New Config" page. This can be freely changed in a new version of a
     *              connector
     */
    public Descriptor(String id, String label) {
        this.id = id;
        this.label = label;
    }

    /**
     * get the Connector ID. Once defined, the ID should not be changed in the connectors to avoid breaking compatibility.
     */
    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }
}
