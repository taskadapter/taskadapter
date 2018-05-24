package com.taskadapter.connector.jira

import com.taskadapter.model.Summary
import com.taskadapter.model._

object JiraField {

  /* newer JIRA versions (like 6.4.11) does not have "timetracking" field
     enabled for tasks by default. let's unselect this field by default
     to avoid user confusion.
   */
  val fields = List(Components, Summary, TaskStatus, Description, TaskType, EstimatedTime, Assignee,
    CreatedOn, DueDate, Priority, Reporter)

  /**
    * Estimated time is not a part of standard JIRA 7 anymore.
    * also removed "Reporter" because export to JIRA gives
    * "status=400, errors={reporter=Field 'reporter' cannot be set. It is not on the appropriate screen, or unknown."
    */
  val defaultFieldsForNewConfig = fields.filter(_ != EstimatedTime).filter(_ != Reporter)
}
