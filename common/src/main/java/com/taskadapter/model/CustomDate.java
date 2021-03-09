package com.taskadapter.model;

import java.util.Date;

public class CustomDate extends Field<Date> implements CanBeLoadedFromString<Date> {
    public CustomDate(String name) {
        super(name);
    }

    @Override
    public FieldParser<Date> getStringValueParser() {
        return new DateTypeTag();
    }

    @Override
    public String toString() {
        return "CustomDate(" + fieldName + ')';
    }
}
