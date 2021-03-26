package com.taskadapter.reporting;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * strips some irrelevant stacktrace elements, like Java internals
 */
public class StacktraceCleaner {
    private static final Set<String> strings = Set.of("java.util.concurrent", "java.lang.Thread");
    private static final String regexp = strings.stream()
            .map(s -> ".*(" + s + ").*")
            .collect(Collectors.joining("|"));

    private static final String replacement = "/./";

    public static String stripInternalStacktraceItems(String stackTrace) {
        var strings = stackTrace
                .split(System.lineSeparator());
        return Arrays.stream(strings)
                .map(line -> {
                    var trimmed = line.trim();

                    if (trimmed.matches(regexp)) {
                        return replacement;
                    } else {
                        return line;
                    }
                })
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
