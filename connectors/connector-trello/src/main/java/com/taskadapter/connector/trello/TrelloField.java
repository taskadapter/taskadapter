package com.taskadapter.connector.trello;

import com.taskadapter.model.Description$;
import com.taskadapter.model.DueDate$;
import com.taskadapter.model.Field;
import com.taskadapter.model.Id$;
import com.taskadapter.model.Key$;
import com.taskadapter.model.ReporterFullName$;
import com.taskadapter.model.ReporterLoginName$;
import com.taskadapter.model.Summary$;
import com.taskadapter.model.TaskStatus$;
import com.taskadapter.model.UpdatedOn$;

import java.util.ArrayList;
import java.util.List;

public class TrelloField {
    public static final Field<String> listId = Field.apply("List Id");
    public static final Field<String> listName = TaskStatus$.MODULE$;

    public static List<Field<?>> fields = List.of(listName, listId,
            Id$.MODULE$,
            Key$.MODULE$,
            Summary$.MODULE$,
            ReporterFullName$.MODULE$,
            ReporterLoginName$.MODULE$,
            Description$.MODULE$,
            DueDate$.MODULE$,
            UpdatedOn$.MODULE$
    );

    public static List<Field<?>> excludeFromNewConfig = List.of(Id$.MODULE$,
            Key$.MODULE$,
            listId,
            ReporterFullName$.MODULE$,
            ReporterLoginName$.MODULE$,
            UpdatedOn$.MODULE$);

    public static List<Field<?>> defaultFieldsForNewConfig() {
        var fieldsCopy = new ArrayList<>(fields);
        fieldsCopy.removeAll(excludeFromNewConfig);
        return fieldsCopy;
    }
}
