package com.taskadapter.connector.jira;

import com.taskadapter.model.AssigneeLoginName$;
import com.taskadapter.model.Components$;
import com.taskadapter.model.CreatedOn$;
import com.taskadapter.model.Description$;
import com.taskadapter.model.DueDate$;
import com.taskadapter.model.EstimatedTime$;
import com.taskadapter.model.Field;
import com.taskadapter.model.Id$;
import com.taskadapter.model.Key$;
import com.taskadapter.model.Priority$;
import com.taskadapter.model.ReporterLoginName$;
import com.taskadapter.model.Summary$;
import com.taskadapter.model.TaskStatus$;
import com.taskadapter.model.TaskType$;

import java.util.ArrayList;
import java.util.List;

public class JiraField {

    /* newer JIRA versions (like 6.4.11) does not have "timetracking" field
       enabled for tasks by default. let's unselect this field by default
       to avoid user confusion.
     */
    public static List<Field<?>> fields = List.of(Components$.MODULE$,
            Summary$.MODULE$,
            TaskStatus$.MODULE$,
            Description$.MODULE$,
            Id$.MODULE$,
            TaskType$.MODULE$,
            EstimatedTime$.MODULE$,
            AssigneeLoginName$.MODULE$,
            CreatedOn$.MODULE$, DueDate$.MODULE$, Key$.MODULE$, Priority$.MODULE$, ReporterLoginName$.MODULE$);

    private static final List<Field<?>> excludeFromNewConfig = List.of(DueDate$.MODULE$,
            EstimatedTime$.MODULE$,
            Id$.MODULE$,
            Key$.MODULE$,
            ReporterLoginName$.MODULE$);

    /**
     * Estimated time, Reporter, DueDate are not included in standard JIRA 7 anymore.
     */
    public static List<Field<?>> defaultFieldsForNewConfig() {
        var fieldsCopy = new ArrayList<>(fields);
        fieldsCopy.removeAll(excludeFromNewConfig);
        return fieldsCopy;
    }
}
