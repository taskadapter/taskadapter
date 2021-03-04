package com.taskadapter.connector.trello;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.model.AllFields;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TrelloFieldBuilder {
    public static List<FieldRow<?>> getDefault() {
        return Arrays.asList(
                new FieldRow(Optional.of(TrelloField.listName), Optional.of(TrelloField.listName), ""),
                new FieldRow(Optional.of(AllFields.summary()), Optional.of(AllFields.summary()), "")
        );
    }
}
