package com.taskadapter.model;

public interface CanBeLoadedFromString<T> {
    FieldParser<T> getStringValueParser();
}
