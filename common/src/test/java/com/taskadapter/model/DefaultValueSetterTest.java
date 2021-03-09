package com.taskadapter.model;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.definition.TaskId;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultValueSetterTest {
    static List<FieldRow<?>> defaultRows = List.of(
            new FieldRow(Optional.of(AllFields.summary), Optional.of(AllFields.summary), ""),
            new FieldRow(Optional.of(AllFields.description), Optional.of(AllFields.description), "")
    );

    @Test
    public void taskIsDeepCloned() {
        var originalTask = new GTask()
                .setValue(AllFields.description, "original description");
        var newTask = DefaultValueSetter.adapt(defaultRows, originalTask);
        originalTask.setValue(AllFields.description, "new description");
        assertThat(newTask.getValue(AllFields.description)).isEqualTo("original description");
    }

    @Test
    public void defaultValueIsSetIfSourceFieldValueIsEmpty() {
        var rows = rows(
                new FieldRow<>(Optional.of(AllFields.summary), Optional.of(AllFields.summary), ""),
                new FieldRow<>(Optional.of(AllFields.description), Optional.of(AllFields.description), "default description")
        );
        var originalTask = new GTask()
                .setValue(AllFields.description, "");

        var newTask = DefaultValueSetter.adapt(rows, originalTask);
        assertThat(newTask.getValue(AllFields.description)).isEqualTo("default description");
    }


    // TA supports one-sided mappings where a default value is provided to the target Field and there is
    // no corresponding Field in the source connector
    @Test
    public void defaultValueIsSetIfSourceFieldIsNotDefinedButDefaultValueExists() {
        var rows = rows(
                new FieldRow(Optional.empty(), Optional.of(AllFields.description), "default description")
        );
        var originalTask = new GTask()
                .setValue(AllFields.description, "");

        var newTask = DefaultValueSetter.adapt(rows, originalTask);
        assertThat(newTask.getValue(AllFields.description)).isEqualTo("default description");
    }

    @Test
    public void fieldIsSafelySkippedIfTargetFieldIsNotDdefined() {
        var rows = rows(
                new FieldRow(Optional.of(AllFields.description), Optional.empty(), "default"));
        var newTask = DefaultValueSetter.adapt(rows,
                new GTask().setValue(AllFields.description, ""));
        assertThat(newTask.getValue(AllFields.description)).isNull();
    }

    @Test
    public void existingValueIsPreservedWhenFieldHasIt() {
        var rows = rows(
                new FieldRow(Optional.of(AllFields.summary), Optional.of(AllFields.summary), ""),
                new FieldRow(Optional.of(AllFields.description), Optional.of(AllFields.description), "default description")
        );
        var originalTask = new GTask()
                .setValue(AllFields.description, "something");
        var newTask = DefaultValueSetter.adapt(rows, originalTask);
        assertThat(newTask.getValue(AllFields.description)).isEqualTo("something");
    }

    // without this creating subtasks won't work, at least in JIRA
    @Test
    public void parentKeyIsPreserved() {
        var task = new GTask();
        var identity = new TaskId(1L, "parent1");
        task.setParentIdentity(identity);
        var newTask = DefaultValueSetter.adapt(defaultRows, task);
        assertThat(newTask.getParentIdentity()).isEqualTo(identity);
    }

    // regression test for https://bitbucket.org/taskadapter/taskadapter/issues/85/subtasks-are-not-saved
    @Test
    public void childrenArePreserved() {
        var parent = new GTask()
                .setId(1L);
        var sub = new GTask()
                .setId(100L);
        parent.addChildTask(sub);

        var adapted = DefaultValueSetter.adapt(defaultRows, parent);
        assertThat(adapted.getChildren()).hasSize(1);
    }

    @Test
    public void dateFieldTypeIsAdapted() {
        var rows = rows(
                new FieldRow(Optional.of(AllFields.dueDate), Optional.of(AllFields.dueDate), null)
        );
        var task = new GTask();
        var date = new Date();
        task.setValue(AllFields.dueDate, date);
        var newTask = DefaultValueSetter.adapt(rows, task);
        assertThat(newTask.getValue(AllFields.dueDate)).isEqualTo(date);
    }

    @Test
    public void setsDefaultValuesWithProperTypes() {
        checkField(AllFields.assigneeLoginName, "login", "login");
        checkField(AllFields.assigneeFullName, "name", "name");
        checkField(AllFields.priority, "1", 1);
        checkDate(AllFields.closedOn, "2018 05 04");
        checkField(AllFields.components, "c1 c2", List.of("c1", "c2"));
        checkField(AllFields.components, "c1", List.of("c1"));
        checkDate(new CustomDate("date1"), "2018 05 04");
        checkField(new CustomFloat("float"), "1.2", 1.2f);
        checkField(new CustomListString("elements"), "a b", List.of("a", "b"));
        checkField(new CustomString("custom1"), "text", "text");
        checkDate(AllFields.createdOn, "2018 05 04");
        checkField(AllFields.description, "text", "text");
        checkDate(AllFields.dueDate, "2018 05 04");
        checkField(AllFields.dueDate, "", null);
        checkField(AllFields.doneRatio, "33", 33F);
        checkField(AllFields.estimatedTime, "10.5", 10.5f);
//    checkField(SpentTime, "3.3", 3.3f);
        checkField(AllFields.id, "5", 5L);
        checkField(AllFields.key, "TEST-1", "TEST-1");
        checkDate(AllFields.startDate, "2018 05 04");
        checkField(AllFields.summary, "text", "text");
        checkField(AllFields.targetVersion, "1.0", "1.0");
        checkField(AllFields.taskStatus, "new", "new");
        checkField(AllFields.taskType, "feature", "feature");
        checkField(AllFields.reporterLoginName, "login", "login");
        checkField(AllFields.reporterFullName, "name", "name");
        checkDate(AllFields.updatedOn, "2018 05 04");
    }

    private static void checkDate(Field<?> field, String str) {
        try {
            checkField(field, str, new DateTypeTag().fromString(str));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void checkField(Field<?> field, String defaultString, Object expectedValue) {
        var rows = rows(
                new FieldRow(Optional.of(field), Optional.of(field), defaultString));
        var newTask = DefaultValueSetter.adapt(rows, new GTask());
        assertThat(newTask.getValue(field)).isEqualTo(expectedValue);
    }


    private static List<FieldRow<?>> rows(FieldRow<?>... rows) {
        return Arrays.asList(rows.clone());
    }
}
