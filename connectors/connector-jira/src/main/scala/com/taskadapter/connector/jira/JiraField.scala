package com.taskadapter.connector.jira

import com.taskadapter.model.Summary
import com.taskadapter.model._

object JiraField {

  /* newer JIRA versions (like 6.4.11) does not have "timetracking" field
     enabled for tasks by default. let's unselect this field by default
     to avoid user confusion.
   */
  val fields = List(Id, Components, Summary, TaskStatus, Description, TaskType, EstimatedTime, Assignee,
    CreatedOn, DueDate, Priority, Reporter)

  // id field is not in the suggested list because typically
  // id from one system cannot be directly used as id in another system.

    // estimated time is not a part of standard JIRA 7 anymore
//    estimatedTime -> EstimatedTime,

    // removing "Reporter" for now because export to JIRA gives
    // "status=400, errors={reporter=Field 'reporter' cannot be set. It is not on the appropriate screen, or unknown."
//    reporter -> Reporter,
}
