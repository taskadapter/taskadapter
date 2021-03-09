package com.taskadapter.model;

public class EstimatedTime extends Field<Float> implements CanBeLoadedFromString<Float> {
    public EstimatedTime() {
        super("EstimatedTime");
    }

    @Override
    public FieldParser<Float> getStringValueParser() {
        return new FloatTypeTag();
    }
}
