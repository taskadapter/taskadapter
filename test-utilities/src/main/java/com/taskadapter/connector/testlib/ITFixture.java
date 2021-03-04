package com.taskadapter.connector.testlib;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.NewConnector;
import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.model.Field;
import com.taskadapter.model.GTask;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ITFixture {
    String targetLocation;
    NewConnector connector;
    Function<TaskId, Void> cleanup;

    public ITFixture(String targetLocation, NewConnector connector, Function<TaskId, Void> cleanup) {
        this.targetLocation = targetLocation;
        this.connector = connector;
        this.cleanup = cleanup;
    }

    public void taskIsCreatedAndLoadedOneField(GTask task, Field<?> fieldNameToSearch) {
        taskIsCreatedAndLoaded(task, Arrays.asList(fieldNameToSearch));
    }

    public void taskIsCreatedAndLoaded(GTask task, List<Field<?>> fields) {
        CommonTestChecks.taskIsCreatedAndLoaded(connector, task, FieldRowBuilder.rows(fields), fields, cleanup);
    }

    public <T> void taskCreatedAndUpdatedOK(List<FieldRow<?>> rows, GTask task, Field<T> fieldToChangeInTest, T newValue) {
        CommonTestChecks.taskCreatedAndUpdatedOK(targetLocation, connector, rows, task, fieldToChangeInTest, newValue, cleanup);
    }

    public <T> void taskCreatedAndUpdatedOK(GTask task, List<FieldWithValue<T>> toUpdate) {
        CommonTestChecks.taskCreatedAndUpdatedOK(targetLocation, connector,
                FieldRowBuilder.rows(toUpdate
                        .stream()
                        .map(fieldWithValue -> fieldWithValue.field)
                        .collect(Collectors.toList())),
                task, toUpdate, cleanup);
    }
}
