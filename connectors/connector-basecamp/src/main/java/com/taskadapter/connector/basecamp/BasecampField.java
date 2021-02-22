package com.taskadapter.connector.basecamp;

import com.taskadapter.model.AssigneeFullName$;
import com.taskadapter.model.CreatedOn$;
import com.taskadapter.model.DoneRatio$;
import com.taskadapter.model.DueDate$;
import com.taskadapter.model.Field;
import com.taskadapter.model.UpdatedOn$;

import java.util.List;

public class BasecampField {
    public static final Field<String> content = Field.apply("Content");

    public static List<Field<?>> fields = List.of(
            content,
            DoneRatio$.MODULE$,
            DueDate$.MODULE$,
            AssigneeFullName$.MODULE$,
            CreatedOn$.MODULE$,
            UpdatedOn$.MODULE$
    );
}
