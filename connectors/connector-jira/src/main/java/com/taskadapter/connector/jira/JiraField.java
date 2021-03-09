package com.taskadapter.connector.jira;

import com.taskadapter.model.AllFields;
import com.taskadapter.model.Field;

import java.util.ArrayList;
import java.util.List;

public class JiraField {

    /* newer JIRA versions (like 6.4.11) do not have "timetracking" field
       enabled for tasks by default. let's unselect this field by default
       to avoid user confusion.
     */
    public static List<Field<?>> fields = List.of(AllFields.components,
            AllFields.summary,
            AllFields.taskStatus,
            AllFields.description,
            AllFields.id,
            AllFields.taskType,
            AllFields.estimatedTime,
            AllFields.assigneeLoginName,
            AllFields.createdOn, AllFields.dueDate, AllFields.key, AllFields.priority, AllFields.reporterLoginName);

    private static final List<Field<?>> excludeFromNewConfig = List.of(AllFields.dueDate,
            AllFields.estimatedTime,
            AllFields.id,
            AllFields.key,
            AllFields.reporterLoginName);

    /**
     * Estimated time, Reporter, DueDate are not included in standard JIRA 7 anymore.
     */
    public static List<Field<?>> defaultFieldsForNewConfig() {
        var fieldsCopy = new ArrayList<>(fields);
        fieldsCopy.removeAll(excludeFromNewConfig);
        return fieldsCopy;
    }
}
