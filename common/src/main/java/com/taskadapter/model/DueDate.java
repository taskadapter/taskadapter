package com.taskadapter.model;

import java.util.Date;

public class DueDate extends Field<Date> implements CanBeLoadedFromString<Date> {
    public DueDate() {
        super("DueDate");
    }

    @Override
    public FieldParser<Date> getStringValueParser() {
        return new DateTypeTag();
    }
}
