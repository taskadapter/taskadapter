package com.taskadapter.connector.trello;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.model.AllFields;
import scala.Option;

import java.util.Arrays;
import java.util.List;

public class TrelloFieldBuilder {
    public static List<FieldRow<?>> getDefault() {
        return Arrays.asList(
                new FieldRow(Option.apply(TrelloField.listName), Option.apply(TrelloField.listName), ""),
                new FieldRow(Option.apply(AllFields.summary()), Option.apply(AllFields.summary()), "")
        );
    }
}
