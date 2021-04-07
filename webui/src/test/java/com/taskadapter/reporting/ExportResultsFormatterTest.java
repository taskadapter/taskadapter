package com.taskadapter.reporting;

import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.web.uiapi.DecodedTaskError;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static com.taskadapter.reporting.ExportResultsFormatter.formatTaskErrors;
import static org.assertj.core.api.Assertions.assertThat;

public class ExportResultsFormatterTest {
    private static final TaskId id1 = new TaskId(1L, "key1");
    private static final TaskId id2 = new TaskId(2L, "key2");
    private static final TaskId id3 = new TaskId(3L, "key3");

    private static final DecodedTaskError item1 = new DecodedTaskError(id1, "error", "exception");
    private static final DecodedTaskError item1SameError = new DecodedTaskError(id2, "error", "exception");
    private static final DecodedTaskError item3 = new DecodedTaskError(id3, "error", "another exception");

    private static final String line = System.lineSeparator();

    @Test
    public void replacesDuplicateExceptions() {
        assertThat(formatTaskErrors(java.util.List.of(item1, item1SameError)))
                .isEqualTo(orig(id1) + line + same(id2));
    }

    @Test
    public void replacesTwoDuplicateExceptions() {
        assertThat(
                formatTaskErrors(List.of(
                        new DecodedTaskError(id1, "error", "exception"),
                        new DecodedTaskError(id2, "error", "exception"),
                        new DecodedTaskError(id3, "error", "exception"))))
                .isEqualTo(
                        orig(id1) + line + same(id2) + line + same(id3));
    }

    @Test
    public void leavesSingleElementAsIs() {
        assertThat(formatTaskErrors(Arrays.asList(item1)))
                .isEqualTo(orig(id1));
    }

    @Test
    public void leavesDifferentElementInPlace() {
        assertThat(formatTaskErrors(List.of(item1, item3)))
                .isEqualTo(orig(id1) + line + other(id3));
    }

    @Test
    public void replacesDuplicateAndLeavesDifferentOneInPlace() {
        assertThat(formatTaskErrors(List.of(item1, item1SameError, item3)))
                .isEqualTo(orig(id1) + line + same(id2) + line + other(id3));
    }

    @Test
    public void emptyListGivesEmptyString() {
        assertThat(formatTaskErrors(List.of())).isEqualTo("");
    }

    private static String orig(TaskId id) {
        return id + " - error - exception";
    }

    private static String same(TaskId id) {
        return id + " - error - same as previous";
    }

    private static String other(TaskId id) {
        return id + " - error - another exception";
    }
}
