package com.taskadapter.webui.config;

import com.vaadin.flow.data.binder.Binder;

import java.util.Objects;

public class EditableFieldMapping {
    private final Binder<EditableFieldMapping> binder;
    private String uniqueIdForTemporaryMap;
    private String fieldInConnector1;
    private String fieldInConnector2;
    private Boolean selected;
    private String defaultValue;

    public EditableFieldMapping(Binder<EditableFieldMapping> binder,
                                String uniqueIdForTemporaryMap, String fieldInConnector1,
                                String fieldInConnector2, Boolean selected, String defaultValue) {
        this.binder = binder;
        this.uniqueIdForTemporaryMap = uniqueIdForTemporaryMap;
        this.fieldInConnector1 = fieldInConnector1;
        this.fieldInConnector2 = fieldInConnector2;
        this.selected = selected;
        this.defaultValue = defaultValue;
    }

    public String getUniqueIdForTemporaryMap() {
        return uniqueIdForTemporaryMap;
    }

    public void setUniqueIdForTemporaryMap(String uniqueIdForTemporaryMap) {
        this.uniqueIdForTemporaryMap = uniqueIdForTemporaryMap;
    }

    public String getFieldInConnector1() {
        return fieldInConnector1;
    }

    public void setFieldInConnector1(String fieldInConnector1) {
        this.fieldInConnector1 = fieldInConnector1;
    }

    public String getFieldInConnector2() {
        return fieldInConnector2;
    }

    public void setFieldInConnector2(String fieldInConnector2) {
        this.fieldInConnector2 = fieldInConnector2;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Binder<EditableFieldMapping> getBinder() {
        return binder;
    }

    @Override
    public String toString() {
        return "EditableFieldMapping{" +
                "uniqueIdForTemporaryMap='" + uniqueIdForTemporaryMap + '\'' +
                ", fieldInConnector1='" + fieldInConnector1 + '\'' +
                ", fieldInConnector2='" + fieldInConnector2 + '\'' +
                ", selected=" + selected +
                ", defaultValue='" + defaultValue + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EditableFieldMapping that = (EditableFieldMapping) o;
        return Objects.equals(binder, that.binder) &&
                Objects.equals(uniqueIdForTemporaryMap, that.uniqueIdForTemporaryMap) &&
                Objects.equals(fieldInConnector1, that.fieldInConnector1) &&
                Objects.equals(fieldInConnector2, that.fieldInConnector2) &&
                Objects.equals(selected, that.selected) &&
                Objects.equals(defaultValue, that.defaultValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(binder, uniqueIdForTemporaryMap, fieldInConnector1, fieldInConnector2, selected, defaultValue);
    }
}
