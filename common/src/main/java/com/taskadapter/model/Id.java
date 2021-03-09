package com.taskadapter.model;

public class Id extends Field<Long> implements CanBeLoadedFromString<Long> {
    public Id() {
        super("Id");
    }

    @Override
    public FieldParser<Long> getStringValueParser() {
        return new LongTypeTag();
    }
}
