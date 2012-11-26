package com.taskadapter.web.ui;

import com.vaadin.data.Property;

/**
 * Constant property.
 * 
 */
public final class ConstProperty implements Property {
    
    private final Object value;
    
    public ConstProperty(Object value) {
        this.value = value;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object newValue) throws ReadOnlyException,
            ConversionException {
        throw new ReadOnlyException();
    }

    @Override
    public Class<?> getType() {
        return value.getClass();
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public void setReadOnly(boolean newStatus) {
        throw new ReadOnlyException();
    }    
}
