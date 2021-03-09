package com.taskadapter.connector;

import com.taskadapter.model.AllFields;

import java.util.List;
import java.util.Optional;

public class TestFieldBuilder {

    public static final FieldRow<?> summaryRow = new FieldRow(
            Optional.of(AllFields.summary),
            Optional.of(AllFields.summary),
            "");

    public static final FieldRow<?> assigneeLoginNameRow = new FieldRow(
            Optional.of(AllFields.assigneeLoginName),
            Optional.of(AllFields.assigneeLoginName),
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
