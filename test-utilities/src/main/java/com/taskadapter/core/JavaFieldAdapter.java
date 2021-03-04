package com.taskadapter.core;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.testlib.FieldRowBuilder;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.AssigneeFullName$;
import com.taskadapter.model.AssigneeLoginName$;
import com.taskadapter.model.CustomString;
import com.taskadapter.model.Description$;
import com.taskadapter.model.Field;
import com.taskadapter.model.Priority$;
import com.taskadapter.model.ReporterLoginName$;
import com.taskadapter.model.Summary$;
import com.taskadapter.model.TaskType$;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class JavaFieldAdapter {
    public static final Optional<Summary$> summaryOpt = Optional.of(AllFields.summary());
    public static final Optional<AssigneeLoginName$> AssigneeLoginNameOpt = Optional.of(AssigneeLoginName$.MODULE$);
    public static final Optional<AssigneeFullName$> AssigneeFullNameOpt = Optional.of(AssigneeFullName$.MODULE$);
    public static final Optional<ReporterLoginName$> ReporterLoginNameOpt = Optional.of(ReporterLoginName$.MODULE$);
    public static final Optional<Description$> descriptionOpt = Optional.of(AllFields.description());
    public static final Optional<Priority$> priorityOpt = Optional.of(AllFields.priority());

    public static final Summary$ summary = AllFields.summary();
    public static final TaskType$ taskType = AllFields.taskType();

    public static Optional<CustomString> customStringOpt(String value) {
        return Optional.of(new CustomString(value));
    }

    public static CustomString customString(String value) {
        return new CustomString(value);
    }

    public static List<FieldRow<?>> rows(Field<?>... fields) {
        return FieldRowBuilder.rows(
                Arrays.asList(fields)
        );
    }
}
