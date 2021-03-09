package com.taskadapter.model;

public class Summary extends Field<String> implements CanBeLoadedFromString<String>{
    public Summary() {
        super("Summary");
    }

    @Override
    public FieldParser<String> getStringValueParser() {
        return new StringTypeTag();
    }
}
