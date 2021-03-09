package com.taskadapter.model;

public class TargetVersion extends Field<String> implements CanBeLoadedFromString<String>{
    public TargetVersion() {
        super("TargetVersion");
    }

    @Override
    public FieldParser<String> getStringValueParser() {
        return new StringTypeTag();
    }
}
