package com.taskadapter.reporting;

import com.taskadapter.connector.testlib.TestDataLoader;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StacktraceCleanerTest {
    @Test
    public void removesJavaInternals() {
        var value = TestDataLoader.loadAsString("reporting/stacktrace-with-java-internals.txt");

        assertThat(StacktraceCleaner.stripInternalStacktraceItems(value))
                .isEqualTo(TestDataLoader.loadAsString("reporting/stacktrace-with-java-internals-expected.txt"));
    }

    @Test
    public void removesJavaThread() {
        var value = TestDataLoader.loadAsString("reporting/stacktrace-java-thread.txt");

        assertThat(StacktraceCleaner.stripInternalStacktraceItems(value))
                .isEqualTo(TestDataLoader.loadAsString("reporting/stacktrace-java-thread-expected.txt"));
    }

    @Test
    public void returnsEmptyStringForEmptyInput() {
        assertThat(StacktraceCleaner.stripInternalStacktraceItems(""))
                .isEqualTo("");
    }
}
