package com.taskadapter.connector.definition;

import com.taskadapter.model.GTaskDescriptor;

public final class FieldMapping {
    private GTaskDescriptor.FIELD field;
    private String connector1;
    private String connector2;
    private boolean selected;

    /**
     * Required for JSon serialization.
     */
    public FieldMapping() {
    }

    public FieldMapping(GTaskDescriptor.FIELD field, String connector1, String connector2, boolean selected) {
        this.field = field;
        this.connector1 = connector1;
        this.connector2 = connector2;
        this.selected = selected;
    }

    public GTaskDescriptor.FIELD getField() {
        return field;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setConnector1(String connector1) {
        this.connector1 = connector1;
    }

    public void setConnector2(String connector2) {
        this.connector2 = connector2;
    }

    public String getConnector1() {
        return connector1;
    }

    public String getConnector2() {
        return connector2;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return "FieldMapping{" +
                "field=" + field +
                ", connector1='" + connector1 + '\'' +
                ", connector2='" + connector2 + '\'' +
                ", selected=" + selected +
                '}';
    }
}
