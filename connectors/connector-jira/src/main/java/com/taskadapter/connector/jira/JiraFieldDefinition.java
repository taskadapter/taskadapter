package com.taskadapter.connector.jira;

import java.util.Objects;
import java.util.Optional;

public class JiraFieldDefinition {
    private Long id;
    private String fieldName;

    /**
     * fullIdForSave    e.g. 'customfield_10100'
     */
    private String fullIdForSave;

    /**
     * typeName         sample values: 'array', 'any', 'option', 'progress', 'user', 'number', 'datetype', ...
     */
    private String typeName;

    /**
     * itemsTypeIfArray "null" for any [typeName] other than 'array'. for arrays sample values are:
     * "string","attachment", "version", "component", "version", "issueLinks", "worklog", ...
     */
    private Optional<String> itemsTypeIfArray;

    public JiraFieldDefinition(Long id, String fieldName, String fullIdForSave, String typeName, Optional<String> itemsTypeIfArray) {
        this.id = id;
        this.fieldName = fieldName;
        this.fullIdForSave = fullIdForSave;
        this.typeName = typeName;
        this.itemsTypeIfArray = itemsTypeIfArray;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Long getId() {
        return id;
    }

    public String getFullIdForSave() {
        return fullIdForSave;
    }

    public Optional<String> getItemsTypeIfArray() {
        return itemsTypeIfArray;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JiraFieldDefinition that = (JiraFieldDefinition) o;
        return Objects.equals(id, that.id) && Objects.equals(fieldName, that.fieldName) && Objects.equals(fullIdForSave, that.fullIdForSave) && Objects.equals(typeName, that.typeName) && Objects.equals(itemsTypeIfArray, that.itemsTypeIfArray);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fieldName, fullIdForSave, typeName, itemsTypeIfArray);
    }

    @Override
    public String toString() {
        return "JiraFieldDefinition{" +
                "id=" + id +
                ", fieldName='" + fieldName + '\'' +
                ", fullIdForSave='" + fullIdForSave + '\'' +
                ", typeName='" + typeName + '\'' +
                ", itemsTypeIfArray=" + itemsTypeIfArray +
                '}';
    }
}
