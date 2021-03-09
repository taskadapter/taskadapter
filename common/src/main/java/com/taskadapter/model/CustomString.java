package com.taskadapter.model;

public class CustomString extends Field<String> implements CanBeLoadedFromString<String>{
    public CustomString(String name) {
        super(name);
    }

    @Override
    public FieldParser<String> getStringValueParser() {
        return new StringTypeTag();
    }
}
