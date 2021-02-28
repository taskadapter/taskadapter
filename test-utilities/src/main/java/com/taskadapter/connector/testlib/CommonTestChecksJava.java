package com.taskadapter.connector.testlib;

import com.taskadapter.connector.definition.TaskId;

import java.util.function.Function;

public class CommonTestChecksJava {
    public static Function<TaskId, Void> skipCleanup = taskId -> null;
}
