package com.taskadapter.connector.definition;

import com.taskadapter.model.GTaskDescriptor;

public final class FieldMapping {
    private GTaskDescriptor.FIELD field;
    private String connector1;
    private String connector2;
    private boolean selected;
    private String defaultValue;

    /**
     * Required for JSon serialization.
     */
    public FieldMapping() {
    }

    public FieldMapping(GTaskDescriptor.FIELD field, String connector1, String connector2, boolean selected, String defaultValue) {
        this.field = field;
        this.connector1 = connector1;
        this.connector2 = connector2;
        this.selected = selected;
        this.defaultValue = defaultValue;
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

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        return "FieldMapping{" +
                "field=" + field +
                ", connector1='" + connector1 + '\'' +
                ", connector2='" + connector2 + '\'' +
                ", selected=" + selected +
                ", defaultValue=" + defaultValue +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldMapping that = (FieldMapping) o;

        if (selected != that.selected) return false;
        if (connector1 != null ? !connector1.equals(that.connector1) : that.connector1 != null) return false;
        if (connector2 != null ? !connector2.equals(that.connector2) : that.connector2 != null) return false;
        if (defaultValue != null ? !defaultValue.equals(that.defaultValue) : that.defaultValue != null) return false;
        if (field != that.field) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = field != null ? field.hashCode() : 0;
        result = 31 * result + (connector1 != null ? connector1.hashCode() : 0);
        result = 31 * result + (connector2 != null ? connector2.hashCode() : 0);
        result = 31 * result + (selected ? 1 : 0);
        result = 31 * result + (defaultValue != null ? defaultValue.hashCode() : 0);
        return result;
    }
}
