package com.taskadapter.model;

public class ReporterFullName extends Field<String> implements CanBeLoadedFromString<String> {
    public ReporterFullName() {
        super("ReporterFullName");
    }

    @Override
    public FieldParser<String> getStringValueParser() {
        return new StringTypeTag();
    }
}
