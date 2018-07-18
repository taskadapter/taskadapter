package com.taskadapter.connector.redmine

import com.taskadapter.model._

object RedmineField {
  val category = Components

  def fields = List(
    category,
    Summary,
    Description,
    Id,
    TaskType,
    EstimatedTime,
    DoneRatio,
    AssigneeFullName,
    AssigneeLoginName,
    DueDate,
    Key,
    ReporterFullName,
    ReporterLoginName,
    StartDate,
    CreatedOn,
    UpdatedOn,
    TaskStatus,
    TargetVersion,
    Priority)

  val excludeFromNewConfig = Seq(UpdatedOn, Id, Key)
  val defaultFieldsForNewConfig = fields.diff(excludeFromNewConfig)

}
