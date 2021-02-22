package com.taskadapter.connector.redmine;

import com.taskadapter.model.AssigneeFullName$;
import com.taskadapter.model.AssigneeLoginName$;
import com.taskadapter.model.Components$;
import com.taskadapter.model.CreatedOn$;
import com.taskadapter.model.Description$;
import com.taskadapter.model.DoneRatio$;
import com.taskadapter.model.DueDate$;
import com.taskadapter.model.EstimatedTime$;
import com.taskadapter.model.Field;
import com.taskadapter.model.Id$;
import com.taskadapter.model.Key$;
import com.taskadapter.model.Priority$;
import com.taskadapter.model.ReporterFullName$;
import com.taskadapter.model.ReporterLoginName$;
import com.taskadapter.model.StartDate$;
import com.taskadapter.model.Summary$;
import com.taskadapter.model.TargetVersion$;
import com.taskadapter.model.TaskStatus$;
import com.taskadapter.model.TaskType$;
import com.taskadapter.model.UpdatedOn$;
import scala.collection.Seq;

import java.util.ArrayList;
import java.util.List;

public class RedmineField {

    public static final Field<Seq<String>> category = Components$.MODULE$;

    public static List<Field<?>> fields = List.of(
            category,
            Summary$.MODULE$,
            Description$.MODULE$,
            Id$.MODULE$,
            TaskType$.MODULE$,
            EstimatedTime$.MODULE$,
            DoneRatio$.MODULE$,
            AssigneeFullName$.MODULE$,
            AssigneeLoginName$.MODULE$,
            DueDate$.MODULE$,
            Key$.MODULE$,
            ReporterFullName$.MODULE$,
            ReporterLoginName$.MODULE$,
            StartDate$.MODULE$,
//    SpentTime,
            CreatedOn$.MODULE$,
            UpdatedOn$.MODULE$,
            TaskStatus$.MODULE$,
            TargetVersion$.MODULE$,
            Priority$.MODULE$);

    public static List<Field<?>> excludeFromNewConfig = List.of(UpdatedOn$.MODULE$, Id$.MODULE$, Key$.MODULE$
            // SpentTime is only supported for load, not save. it is better to skip it in default configs
//    SpentTime
    );

    public static List<Field<?>> defaultFieldsForNewConfig() {
        var fieldsCopy = new ArrayList<>(fields);
        fieldsCopy.removeAll(excludeFromNewConfig);
        return fieldsCopy;
    }
}

