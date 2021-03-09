package com.taskadapter.model;

import com.taskadapter.connector.definition.TaskId;

import java.util.Date;
import java.util.List;

public class AllFields {
    public static Summary summary = new Summary();
    public static Description description = new Description();
    public static Field<Long> id = new Id();
    public static Field<String> key = new Key();
    public static Field<TaskId> parentKey = new ParentKey();
    public static Field<TaskId> sourceSystemId = new SourceSystemId();
    public static Field<List<GTask>> children = new Children();
    public static Field<List<GRelation>> relations = new Relations();
    public static Field<String> taskType = new TaskType();
    public static Field<Float> doneRatio = new DoneRatio();
    public static DueDate dueDate = new DueDate();
    public static Priority priority = new Priority();
    public static AssigneeLoginName assigneeLoginName = new AssigneeLoginName();
    public static AssigneeFullName assigneeFullName = new AssigneeFullName();
    public static Field<Date> createdOn = new CreatedOn();
    public static Field<Date> updatedOn = new UpdatedOn();
    public static Field<Date> closedOn = new ClosedOn();
    public static Field<Date> startDate = new StartDate();
    public static Components components = new Components();
    public static Field<Float> estimatedTime = new EstimatedTime();
    public static Field<String> targetVersion = new TargetVersion();
    public static Field<String> taskStatus = new TaskStatus();
    public static ReporterLoginName reporterLoginName = new ReporterLoginName();
    public static ReporterFullName reporterFullName = new ReporterFullName();
}
