package com.taskadapter.model;

public class AssigneeFullName extends Field<String> implements CanBeLoadedFromString<String>{
    public AssigneeFullName() {
        super("AssigneeFullName");
    }

    @Override
    public FieldParser<String> getStringValueParser() {
        return new StringTypeTag();
    }
}
