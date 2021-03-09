package com.taskadapter.connector.trello;

import com.taskadapter.model.AllFields;
import com.taskadapter.model.CustomString;
import com.taskadapter.model.Field;

import java.util.ArrayList;
import java.util.List;

public class TrelloField {
    public static final Field<String> listId = new CustomString("List Id");
    public static final Field<String> listName = AllFields.taskStatus;

    public static List<Field<?>> fields = List.of(listName, listId,
            AllFields.id,
            AllFields.key,
            AllFields.summary,
            AllFields.reporterFullName,
            AllFields.reporterLoginName,
            AllFields.description,
            AllFields.dueDate,
            AllFields.updatedOn
    );

    public static List<Field<?>> excludeFromNewConfig = List.of(
            AllFields.id,
            AllFields.key,
            listId,
            AllFields.reporterFullName,
            AllFields.reporterLoginName,
            AllFields.updatedOn);

    public static List<Field<?>> defaultFieldsForNewConfig() {
        var fieldsCopy = new ArrayList<>(fields);
        fieldsCopy.removeAll(excludeFromNewConfig);
        return fieldsCopy;
    }
}
