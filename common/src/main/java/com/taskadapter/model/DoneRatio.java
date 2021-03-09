package com.taskadapter.model;

public class DoneRatio extends Field<Float> implements CanBeLoadedFromString<Float> {
    public DoneRatio() {
        super("DoneRatio");
    }

    @Override
    public FieldParser<Float> getStringValueParser() {
        return new FloatTypeTag();
    }
}
