package com.taskadapter.connector.definition;

import com.taskadapter.model.GTaskDescriptor;

public final class FieldMapping {
    private GTaskDescriptor.FIELD field;
    private String left;
    private String right;
    private boolean selected;

    /**
     * Required for JSon serialization.
     */
    public FieldMapping() {
    }

    public FieldMapping(GTaskDescriptor.FIELD field, String left, String right, boolean selected) {
        this.field = field;
        this.left = left;
        this.right = right;
        this.selected = selected;
    }

    public GTaskDescriptor.FIELD getField() {
        return field;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public void setRight(String right) {
        this.right = right;
    }

    public String getLeft() {
        return left;
    }

    public String getRight() {
        return right;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
