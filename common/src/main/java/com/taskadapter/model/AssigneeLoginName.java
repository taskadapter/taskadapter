package com.taskadapter.model;

public class AssigneeLoginName extends Field<String> implements CanBeLoadedFromString<String>{
    public AssigneeLoginName() {
        super("AssigneeLoginName");
    }

    @Override
    public FieldParser<String> getStringValueParser() {
        return new StringTypeTag();
    }
}
