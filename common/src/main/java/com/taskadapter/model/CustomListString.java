package com.taskadapter.model;

import java.util.List;

public class CustomListString extends Field<List<String>> implements CanBeLoadedFromString<List<String>> {
    public CustomListString(String name) {
        super(name);
    }

    @Override
    public FieldParser<List<String>> getStringValueParser() {
        return new ListStringTypeTag();
    }
}
