package com.taskadapter.connector.definition;

public class Mapping {
    private boolean selected;
    private String currentValue;

    public String getCurrentValue() {
        return currentValue;
    }

    public Mapping(boolean selected) {
        this.selected = selected;
    }

    public Mapping() {
        this(true);
    }

    public Mapping(boolean selected, String currentValue) {
        this(selected);
        this.currentValue = currentValue;
    }

    public boolean isSelected() {
        return selected;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((currentValue == null) ? 0 : currentValue.hashCode());
        result = prime * result + (selected ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Mapping other = (Mapping) obj;
        if (currentValue == null) {
            if (other.currentValue != null) {
                return false;
            }
        } else if (!currentValue.equals(other.currentValue)) {
            return false;
        }
        if (selected != other.selected) {
            return false;
        }
        return true;
    }
}
