package com.taskadapter.web.ui;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.AbstractProperty;

/**
 * Absolutely read-only property.
 * 
 */
public final class AbsoluteReadonlyProperty extends AbstractProperty {

    /**
     * Peer property.
     */
    private final Property peer;

    public AbsoluteReadonlyProperty(Property peer) {
        this.peer = peer;
        super.setReadOnly(true);
        if (peer instanceof Property.ValueChangeNotifier) {
            Property.ValueChangeNotifier vcn = (Property.ValueChangeNotifier) peer;
            vcn.addListener(new ValueChangeListener() {
                @Override
                public void valueChange(ValueChangeEvent event) {
                    fireValueChange();
                }
            });
        }
    }

    @Override
    public Object getValue() {
        return peer.getValue();
    }

    @Override
    public void setValue(Object newValue) throws ReadOnlyException,
            ConversionException {
        throw new ReadOnlyException();
    }

    @Override
    public Class<?> getType() {
        return peer.getType();
    }
    
    @Override
    public void setReadOnly(boolean newStatus) {
        throw new ReadOnlyException();
    }

}
