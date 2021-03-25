package com.taskadapter.reporting;

import com.taskadapter.web.uiapi.DecodedTaskError;
import com.taskadapter.webui.results.ExportResultFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExportResultsFormatter {
    public static String toNiceString(ExportResultFormat result) {
        return System.lineSeparator()
                + "Tasks created: " + result.getCreatedTasksNumber() + System.lineSeparator() +
                "Tasks updated: " + result.getUpdatedTasksNumber() + System.lineSeparator() +
                "General errors: " + result.getGeneralErrors() + System.lineSeparator() +
                "Task-specific errors: " + System.lineSeparator() + formatTaskErrors(result.getTaskErrors());
    }

    static String formatTaskErrors(List<DecodedTaskError> errors) {
        if (errors.isEmpty()) {
            return "";
        }

        var results = new ArrayList<DecodedTaskError>();
        results.add(errors.get(0));

        for (int i = 1; i < errors.size(); i++) {
            var prev = errors.get(i - 1);
            var current = errors.get(i);

            var newItem = current.exceptionStackTrace.equals(prev.exceptionStackTrace) ?
                    new DecodedTaskError(current.sourceSystemTaskId, current.connector2ErrorText, "same as previous")
                    :
                    new DecodedTaskError(current.sourceSystemTaskId, current.connector2ErrorText,
                            StacktraceCleaner.stripInternalStacktraceItems(current.exceptionStackTrace));

            results.add(newItem);
        }
        final List<String> list = results.stream().map(e -> e.sourceSystemTaskId + " - " + e.connector2ErrorText + " - " + e.exceptionStackTrace).collect(Collectors.toList());
        return String.join(System.lineSeparator(), list);
    }
}
