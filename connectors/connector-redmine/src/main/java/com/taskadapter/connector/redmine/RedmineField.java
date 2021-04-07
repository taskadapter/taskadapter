package com.taskadapter.connector.redmine;

import com.taskadapter.model.AllFields;
import com.taskadapter.model.Field;

import java.util.ArrayList;
import java.util.List;

import static com.taskadapter.model.AllFields.components;

public class RedmineField {

    public static final Field<List<String>> category = components;

    public static List<Field<?>> fields = List.of(
            category,
            AllFields.summary,
            AllFields.description,
            AllFields.id,
            AllFields.taskType,
            AllFields.estimatedTime,
            AllFields.doneRatio,
            AllFields.assigneeFullName,
            AllFields.assigneeLoginName,
            AllFields.dueDate,
            AllFields.key,
            AllFields.reporterFullName,
            AllFields.reporterLoginName,
            AllFields.startDate,
//    SpentTime,
            AllFields.createdOn,
            AllFields.updatedOn,
            AllFields.taskStatus,
            AllFields.targetVersion,
            AllFields.priority);

    public static List<Field<?>> excludeFromNewConfig = List.of(AllFields.updatedOn, AllFields.id, AllFields.key
            // SpentTime is only supported for load, not save. it is better to skip it in default configs
//    SpentTime
    );

    public static List<Field<?>> defaultFieldsForNewConfig() {
        var fieldsCopy = new ArrayList<>(fields);
        fieldsCopy.removeAll(excludeFromNewConfig);
        return fieldsCopy;
    }
}

