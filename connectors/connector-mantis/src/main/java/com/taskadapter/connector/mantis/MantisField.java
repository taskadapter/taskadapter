package com.taskadapter.connector.mantis;

import com.taskadapter.model.AssigneeFullName$;
import com.taskadapter.model.AssigneeLoginName$;
import com.taskadapter.model.CreatedOn$;
import com.taskadapter.model.Description$;
import com.taskadapter.model.DueDate$;
import com.taskadapter.model.Field;
import com.taskadapter.model.Priority$;
import com.taskadapter.model.Summary$;
import com.taskadapter.model.UpdatedOn$;

import java.util.List;

public class MantisField {
    public static List<Field<?>> fields = List.of(Summary$.MODULE$,
            Description$.MODULE$,
            AssigneeFullName$.MODULE$,
            AssigneeLoginName$.MODULE$,
            DueDate$.MODULE$,
            CreatedOn$.MODULE$,
            UpdatedOn$.MODULE$,
            Priority$.MODULE$);
}
