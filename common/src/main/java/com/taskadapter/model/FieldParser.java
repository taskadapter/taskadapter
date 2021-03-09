package com.taskadapter.model;

// TODO 14 the interface part is not used, all "tag" classes are currently caller directly anyway.
//  see com.taskadapter.model.ClosedOn
public interface FieldParser<T> {
    T fromString(String str);
}
