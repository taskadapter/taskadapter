package com.taskadapter.connector.basecamp.classic;

import com.taskadapter.model.AllFields;
import com.taskadapter.model.CustomString;
import com.taskadapter.model.Field;

import java.util.List;

public class BasecampClassicField {

    public static final Field<String> content = new CustomString("Content");

    public static List<Field<?>> fields = List.of(
            content,
            AllFields.doneRatio,
            AllFields.dueDate,
            AllFields.assigneeFullName,
            AllFields.createdOn,
            AllFields.updatedOn,
            AllFields.closedOn);
}
