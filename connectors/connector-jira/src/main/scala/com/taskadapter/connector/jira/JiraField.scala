package com.taskadapter.connector.jira

import com.taskadapter.model.Summary
import com.taskadapter.model._

object JiraField {

  /* newer JIRA versions (like 6.4.11) does not have "timetracking" field
     enabled for tasks by default. let's unselect this field by default
     to avoid user confusion.
   */
  val fields = List(Components, Summary, TaskStatus, Description, Id, TaskType, EstimatedTime, AssigneeLoginName,
    CreatedOn, DueDate, Key, Priority, ReporterLoginName,
    SpentTime)

  val excludeFromNewConfig = Seq(DueDate, EstimatedTime, Id, Key, ReporterLoginName, SpentTime)

  /**
    * Estimated time, Reporter, DueDate are not included in standard JIRA 7 anymore.
    */
  val defaultFieldsForNewConfig = fields.diff(excludeFromNewConfig)
}
