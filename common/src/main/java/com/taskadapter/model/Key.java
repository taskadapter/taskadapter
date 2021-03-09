package com.taskadapter.model;

public class Key extends Field<String> implements CanBeLoadedFromString<String> {
    public Key() {
        super("Key");
    }

    @Override
    public FieldParser<String> getStringValueParser() {
        return new StringTypeTag();
    }
}
