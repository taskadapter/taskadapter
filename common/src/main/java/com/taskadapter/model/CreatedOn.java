package com.taskadapter.model;

import java.util.Date;

public class CreatedOn extends Field<Date> implements CanBeLoadedFromString<Date> {
    public CreatedOn() {
        super("CreatedOn");
    }

    @Override
    public FieldParser<Date> getStringValueParser() {
        return new DateTypeTag();
    }
}
