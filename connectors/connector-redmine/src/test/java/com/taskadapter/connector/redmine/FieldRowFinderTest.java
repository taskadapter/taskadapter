package com.taskadapter.connector.redmine;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.core.JavaFieldAdapter;
import com.taskadapter.model.AllFields;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static com.taskadapter.core.JavaFieldAdapter.summaryOpt;
import static org.assertj.core.api.Assertions.assertThat;

public class FieldRowFinderTest {
    @Test
    public void found() {
        List<FieldRow<?>> rows = List.of(
                new FieldRow(
                        summaryOpt,
                        summaryOpt,
                        "default"
                ),
                new FieldRow(
                        JavaFieldAdapter.priorityOpt,
                        JavaFieldAdapter.priorityOpt,
                        "default"
                ));
        assertThat(FieldRowFinder.containsTargetField(rows, AllFields.priority))
                .isTrue();
    }

    @Test
    public void notFound() {
        List<FieldRow<?>> rows = List.of(new FieldRow(
                summaryOpt,
                summaryOpt,
                "default"
        ));
        assertThat(FieldRowFinder.containsTargetField(rows, AllFields.priority))
                .isFalse();
    }

    @Test
    public void sourcePresentAndTargetMissingDoesNotLeadToException() {
        List<FieldRow<?>> rows = List.of(new FieldRow(
                summaryOpt,
                Optional.empty(),
                ""
        ));
        assertThat(FieldRowFinder.containsTargetField(rows, AllFields.priority))
                .isFalse();
    }

}