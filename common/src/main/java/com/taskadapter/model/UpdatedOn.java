package com.taskadapter.model;

import java.util.Date;

public class UpdatedOn extends Field<Date> implements CanBeLoadedFromString<Date> {
    public UpdatedOn() {
        super("UpdatedOn");
    }

    @Override
    public FieldParser<Date> getStringValueParser() {
        return new DateTypeTag();
    }
}
