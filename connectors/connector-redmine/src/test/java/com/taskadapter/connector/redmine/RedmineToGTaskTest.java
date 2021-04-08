package com.taskadapter.connector.redmine;

import com.taskadapter.connector.Priorities;
import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.GRelationType;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueRelation;
import com.taskadapter.redmineapi.bean.Tracker;
import org.junit.Test;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RedmineToGTaskTest {
    private final RedmineUserCache userCache = new RedmineUserCache(List.of());

    private RedmineToGTask get() {
        var config = new RedmineConfig();
        return new RedmineToGTask(config, userCache);
    }

    @Test
    public void summaryIsConverted() {
        var redmineIssue = new Issue();
        redmineIssue.setSubject("text 1");
        var task = get().convertToGenericTask(redmineIssue);
        assertThat(task.getValue(AllFields.summary)).isEqualTo("text 1");
    }

    @Test
    public void descriptionIsConverted() {
        var redmineIssue = new Issue();
        redmineIssue.setDescription("description 1");
        var task = get().convertToGenericTask(redmineIssue);
        assertThat(task.getValue(AllFields.description)).isEqualTo("description 1");
    }

    @Test
    public void idIsConvertedIfSet() {
        var redmineIssue = new Issue().setId(123);
        var task = get().convertToGenericTask(redmineIssue);
        assertThat(task.getId()).isEqualTo(123);
    }

    @Test
    public void idIsIgnoredIfNull() {
        var redmineIssue = new Issue();
        var task = get().convertToGenericTask(redmineIssue);
        assertThat(task.getId()).isNull();
    }

    @Test
    public void idIsSetToKey() {
        var redmineIssue = new Issue().setId(123);
        var task = get().convertToGenericTask(redmineIssue);
        assertThat(task.getKey()).isEqualTo("123");
    }

    @Test
    public void parentIdIsConvertedIfSet() {
        var redmineIssue = new Issue();
        redmineIssue.setParentId(123);
        var task = get().convertToGenericTask(redmineIssue);
        assertThat(task.getParentIdentity()).isEqualTo(new TaskId(123L, "123"));
    }

    @Test
    public void parentIdIsIgnoredIfNotSet() {
        var redmineIssue = new Issue();
        var task = get().convertToGenericTask(redmineIssue);
        assertThat(task.getParentIdentity()).isNull();
    }

    @Test
    public void assigneeIsIgnoredIfNotSet() {
        var redmineIssue = new Issue();
        var task = get().convertToGenericTask(redmineIssue);
        assertThat(task.getValue(AllFields.assigneeFullName))
                .isNull();
        assertThat(task.getValue(AllFields.assigneeLoginName))
                .isNull();
    }

    @Test
    public void trackerTypeIsConvertedIfSet() {
        var redmineIssue = new Issue();
        var tracker = new Tracker().setId(123).setName("something");
        redmineIssue.setTracker(tracker);
        var task = get().convertToGenericTask(redmineIssue);
        assertThat(task.getValue(AllFields.taskType)).isEqualTo("something");
    }

    @Test
    public void trackerTypeIsIgnoredIfNotSet() {
        var redmineIssue = new Issue();
        var task = get().convertToGenericTask(redmineIssue);
        assertThat(task.getValue(AllFields.taskType))
                .isNull();
    }

    @Test
    public void statusIsConverted() {
        var redmineIssue = new Issue();
        redmineIssue.setStatusName("some status");
        var task = get().convertToGenericTask(redmineIssue);
        assertThat(task.getValue(AllFields.taskStatus))
                .isEqualTo("some status");
    }

    @Test
    public void estimatedHoursAreConverted() {
        var redmineIssue = new Issue();
        redmineIssue.setEstimatedHours(55f);
        var task = get().convertToGenericTask(redmineIssue);
        assertThat(task.getValue(AllFields.estimatedTime)).isEqualTo(55F);
    }

    @Test
    public void doneRatioIsConverted() {
        var redmineIssue = new Issue();
        redmineIssue.setDoneRatio(75);
        var task = get().convertToGenericTask(redmineIssue);
        assertThat(task.getValue(AllFields.doneRatio)).isEqualTo(75F);
    }

    @Test
    public void startDateIsConverted() {
        var redmineIssue = new Issue();
        var time = getTime();
        redmineIssue.setStartDate(time);
        var task = get().convertToGenericTask(redmineIssue);
        assertThat(task.getValue(AllFields.startDate)).isEqualTo(time);
    }

    private static Date getTime() {
        var calendar = Calendar.getInstance();
        calendar.set(2014, Calendar.APRIL, 23, 0, 0, 0);
        return calendar.getTime();
    }

    @Test
    public void dueDateIsConverted() {
        var redmineIssue = new Issue();
        var time = getTime();
        redmineIssue.setDueDate(time);
        var task = get().convertToGenericTask(redmineIssue);
        assertThat(task.getValue(AllFields.dueDate)).isEqualTo(time);
    }

    @Test
    public void createdOnIsConverted() {
        var redmineIssue = new Issue();
        var time = getTime();
        redmineIssue.setCreatedOn(time);
        var task = get().convertToGenericTask(redmineIssue);
        assertThat(task.getValue(AllFields.createdOn)).isEqualTo(time);
    }

    @Test
    public void updatedOnIsConverted() {
        var redmineIssue = new Issue();
        var time = getTime();
        redmineIssue.setUpdatedOn(time);
        var task = get().convertToGenericTask(redmineIssue);
        assertThat(task.getValue(AllFields.updatedOn)).isEqualTo(time);
    }

    @Test
    public void priorityIsAssignedDefaultValueIfNotSet() {
        var redmineIssue = new Issue();
        var task = get().convertToGenericTask(redmineIssue);
        assertThat(task.getValue(AllFields.priority)).isEqualTo(Priorities.DEFAULT_PRIORITY_VALUE);
    }

    @Test
    public void priorityIsConvertedIfSet() {
        var redmineIssue = new Issue();
        redmineIssue.setPriorityText("High");
        var task = get().convertToGenericTask(redmineIssue);
        assertThat(task.getValue(AllFields.priority)).isEqualTo(700);
    }

    @Test
    public void priorityIsAssignedDefaultValueIfUnknownValueSet() {
        var redmineIssue = new Issue();
        redmineIssue.setPriorityText("some unknown text");
        var task = get().convertToGenericTask(redmineIssue);
        assertThat(task.getValue(AllFields.priority)).isEqualTo(Priorities.DEFAULT_PRIORITY_VALUE);
    }

    @Test
    public void relationsAreConverted() {
        var redmineIssue = new Issue().setId(10);
        var relation = new IssueRelation(null);
        relation.setType(IssueRelation.TYPE.precedes.toString());
        relation.setIssueId(10);
        relation.addIssueToId(20);
        redmineIssue.addRelations(Collections.singletonList(relation));
        var task = get().convertToGenericTask(redmineIssue);
        assertThat(task.getRelations()).hasSize(1);
        var gRelation = task.getRelations().get(0);

        assertThat(gRelation.getTaskId().getId()).isEqualTo(10);
        assertThat(gRelation.getRelatedTaskId().getId()).isEqualTo(20);
        assertThat(gRelation.getType()).isEqualTo(GRelationType.precedes);
    }
}
