package com.taskadapter.model;

public class TaskStatus extends Field<String> implements CanBeLoadedFromString<String>{
    public TaskStatus() {
        super("Status");
    }

    @Override
    public FieldParser<String> getStringValueParser() {
        return new StringTypeTag();
    }
}
