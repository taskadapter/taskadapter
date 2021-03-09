package com.taskadapter.model;

public class Priority extends Field<Integer> implements CanBeLoadedFromString<Integer>{
    public Priority() {
        super("Priority");
    }

    @Override
    public FieldParser<Integer> getStringValueParser() {
        return new IntegerTypeTag();
    }
}
