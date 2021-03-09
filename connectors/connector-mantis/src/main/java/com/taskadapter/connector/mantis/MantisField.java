package com.taskadapter.connector.mantis;

import com.taskadapter.model.AllFields;
import com.taskadapter.model.Field;

import java.util.List;

public class MantisField {
    public static List<Field<?>> fields = List.of(
            AllFields.summary,
            AllFields.description,
            AllFields.assigneeFullName,
            AllFields.assigneeLoginName,
            AllFields.dueDate,
            AllFields.createdOn,
            AllFields.updatedOn,
            AllFields.priority);
}
