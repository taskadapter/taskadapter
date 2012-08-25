package com.taskadapter.web.data;

/**
 * Constant data formatter.
 * 
 * @author maxkar
 * 
 * @param <T>
 */
final class ConstFormatter<T> implements DataFormatter<T> {
    /**
     * Value to convert data to.
     */
    private final String value;
    
    public ConstFormatter(String value) {
        super();
        this.value = value;
    }

    @Override
    public String format(T data) {
        return value;
    }

}
