package com.taskadapter.model;

import java.util.Date;

public class ClosedOn extends Field<Date> implements CanBeLoadedFromString<Date> {
    public ClosedOn() {
        super("ClosedOn");
    }

    @Override
    public FieldParser<Date> getStringValueParser() {
        return new DateTypeTag();
    }
}
