package com.taskadapter.model;

public class Description extends Field<String> implements CanBeLoadedFromString<String> {
    public Description() {
        super("Description");
    }

    @Override
    public FieldParser<String> getStringValueParser() {
        return new StringTypeTag();
    }
}
