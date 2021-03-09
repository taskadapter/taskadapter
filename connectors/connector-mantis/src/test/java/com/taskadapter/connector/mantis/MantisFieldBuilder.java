package com.taskadapter.connector.mantis;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.model.AllFields;

import java.util.List;

public class MantisFieldBuilder {
    public static List<FieldRow<?>> getDefault() {
        return List.of(
                FieldRow.apply(AllFields.summary, AllFields.summary, ""),
                FieldRow.apply(AllFields.description, AllFields.description, "-")
        );
    }
}
