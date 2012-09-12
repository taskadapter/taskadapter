package com.taskadapter.web.configeditor;

import com.taskadapter.connector.Priorities;
import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractProperty;

public class PrioirtyValue extends AbstractProperty implements Property,
        Property.ValueChangeNotifier {

    private static final long serialVersionUID = -865197265809925838L;
    private final Priorities model;
    private final String key;
    private String value;

    /**
     * "Invalid" field value. Set to true iff requested value is invalid and
     * cannot be set as a "priority" value. Changed in "setValue".
     */
    private boolean invalid;
    
    public PrioirtyValue(Priorities model, String key) {
        this.model = model;
        this.key = key;
        this.value = model.getPriorityByText(key).toString();
    }
    
    boolean isValid() {
        return !invalid;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object newValue) throws ReadOnlyException,
            ConversionException {
        if (this.value == newValue || this.value != null
                && this.value.equals(newValue)) {
            return;
        }
        this.value = newValue.toString();
        try {
            final Integer newPriority = Integer.valueOf(value);
            model.setPriority(key, newPriority);
            invalid = false;
        } catch (NumberFormatException e) {
            invalid = true;
        }
    }

    @Override
    public Class<?> getType() {
        return Integer.class;
    }

}
