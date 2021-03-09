package com.taskadapter.model;

import java.util.List;

public class Components extends Field<List<String>> implements CanBeLoadedFromString<List<String>> {
    public Components() {
        super("Components");
    }

    @Override
    public FieldParser<List<String>> getStringValueParser() {
        return new ListStringTypeTag();
    }
}
