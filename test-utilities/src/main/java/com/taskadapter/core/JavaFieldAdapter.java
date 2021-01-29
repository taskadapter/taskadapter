package com.taskadapter.core;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.testlib.FieldRowBuilder;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.AssigneeFullName$;
import com.taskadapter.model.AssigneeLoginName$;
import com.taskadapter.model.CustomString;
import com.taskadapter.model.Description$;
import com.taskadapter.model.Field;
import com.taskadapter.model.ReporterLoginName$;
import com.taskadapter.model.Summary$;
import com.taskadapter.model.TaskType$;
import scala.Option;
import scala.collection.JavaConverters;

import java.util.Arrays;
import java.util.List;

public class JavaFieldAdapter {
    public static final Option<Summary$> summaryOpt = Option.apply(AllFields.summary());
    public static final Option<AssigneeLoginName$> AssigneeLoginNameOpt = Option.apply(AssigneeLoginName$.MODULE$);
    public static final Option<AssigneeFullName$> AssigneeFullNameOpt = Option.apply(AssigneeFullName$.MODULE$);
    public static final Option<ReporterLoginName$> ReporterLoginNameOpt = Option.apply(ReporterLoginName$.MODULE$);
    public static final Option<Description$> descriptionOpt = Option.apply(AllFields.description());

    public static final Summary$ summary = AllFields.summary();
    public static final TaskType$ taskType = AllFields.taskType();

    public static Option<CustomString> customStringOpt(String value) {
        return Option.apply(new CustomString(value));
    }

    public static CustomString customString(String value) {
        return new CustomString(value);
    }

    public static List<FieldRow<?>> rows(Field<?>... fields) {
        return JavaConverters.seqAsJavaList(
                FieldRowBuilder.rows(
                        JavaConverters.asScalaBuffer(Arrays.asList(fields))
                )
        );
    }
}
