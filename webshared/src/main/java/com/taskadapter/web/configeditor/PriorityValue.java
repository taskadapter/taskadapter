package com.taskadapter.web.configeditor;

import com.taskadapter.connector.Priorities;

public class PriorityValue /*extends AbstractProperty implements Property,
        Property.ValueChangeNotifier*/ {

    private final Priorities model;
    private final String key;
    private Integer value;

    /**
     * "Invalid" field value. Set to true iff requested value is invalid and
     * cannot be set as a "priority" value. Changed in "setValue".
     */
    private boolean invalid;
    
    public PriorityValue(Priorities model, String key) {
        this.model = model;
        this.key = key;
        this.value = model.getPriorityByText(key);
    }
    
    boolean isValid() {
        return !invalid;
    }
/*

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object newValue) throws ReadOnlyException,
            Converter.ConversionException {
        if (this.value == newValue || this.value != null
                && this.value.equals(newValue)) {
            return;
        }
        this.value = (Integer) newValue;
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
*/

}
