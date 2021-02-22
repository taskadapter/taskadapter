package com.taskadapter.connector.github;

import com.taskadapter.model.AssigneeLoginName$;
import com.taskadapter.model.CreatedOn$;
import com.taskadapter.model.Description$;
import com.taskadapter.model.Field;
import com.taskadapter.model.StartDate$;
import com.taskadapter.model.Summary$;
import com.taskadapter.model.UpdatedOn$;

import java.util.List;

public class GithubField {
    public static List<Field<?>> fields = List.of(
            Summary$.MODULE$,
            Description$.MODULE$,
            AssigneeLoginName$.MODULE$,
            StartDate$.MODULE$,
            CreatedOn$.MODULE$,
            UpdatedOn$.MODULE$);
}
