package com.taskadapter.model;

import java.util.Date;

public class StartDate extends Field<Date> implements CanBeLoadedFromString<Date> {
    public StartDate() {
        super("StartDate");
    }

    @Override
    public FieldParser<Date> getStringValueParser() {
        return new DateTypeTag();
    }
}
