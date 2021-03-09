package com.taskadapter.model;

public class SpentTime extends Field<Float> implements CanBeLoadedFromString<Float> {
    public SpentTime() {
        super("SpentTime");
    }

    @Override
    public FieldParser<Float> getStringValueParser() {
        return new FloatTypeTag();
    }
}
