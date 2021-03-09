package com.taskadapter.model;

public class TaskType extends Field<String> implements CanBeLoadedFromString<String> {
    public TaskType() {
        super("TaskType");
    }

    @Override
    public FieldParser<String> getStringValueParser() {
        return new StringTypeTag();
    }
}
