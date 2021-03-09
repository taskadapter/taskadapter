package com.taskadapter.connector.github;

import com.taskadapter.model.AllFields;
import com.taskadapter.model.Field;

import java.util.List;

public class GithubField {
    public static List<Field<?>> fields = List.of(
            AllFields.summary,
            AllFields.description,
            AllFields.assigneeLoginName,
            AllFields.startDate,
            AllFields.createdOn,
            AllFields.updatedOn);
}
