package com.taskadapter.model;

public class ReporterLoginName extends Field<String> implements CanBeLoadedFromString<String>{
    public ReporterLoginName() {
        super("ReporterLoginName");
    }

    @Override
    public FieldParser<String> getStringValueParser() {
        return new StringTypeTag();
    }
}
