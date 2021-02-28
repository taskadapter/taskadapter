package com.taskadapter.connector;

import com.taskadapter.model.AllFields;
import scala.Option;

import java.util.List;

public class TestFieldBuilder {

    public static final FieldRow<?> summaryRow = new FieldRow(
            Option.apply(AllFields.summary()),
            Option.apply(AllFields.summary()),
            "");

    public static final FieldRow<?> assigneeLoginNameRow = new FieldRow(
            Option.apply(AllFields.assigneeLoginName()),
            Option.apply(AllFields.assigneeLoginName()),
            null);

    public static List<FieldRow<?>> getSummaryRow() {
        return List.of(
                summaryRow
        );
    }

    public static List<FieldRow<?>> getSummaryAndAssigneeLogin() {
        return List.of(
                summaryRow,
                assigneeLoginNameRow
        );
    }
}
