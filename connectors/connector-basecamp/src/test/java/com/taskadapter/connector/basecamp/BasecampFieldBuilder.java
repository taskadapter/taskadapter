package com.taskadapter.connector.basecamp;

import com.taskadapter.connector.FieldRow;

import java.util.List;

public class BasecampFieldBuilder {
    static List<FieldRow<?>> getDefault() {
        return List.of(
                FieldRow.apply(BasecampField.content, BasecampField.content, "")
        );
    }
}
