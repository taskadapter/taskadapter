package com.taskadapter.model;

public class CustomFloat extends Field<Float> implements CanBeLoadedFromString<Float> {
    public CustomFloat(String name) {
        super(name);
    }

    @Override
    public FieldParser<Float> getStringValueParser() {
        return new FloatTypeTag();
    }

    @Override
    public String toString() {
        return "CustomFloat(" + fieldName + ')';
    }
}
