package com.taskadapter.integrationtests

import com.taskadapter.connector.jira.JiraField
import com.taskadapter.model.{Assignee, GTask, GTaskBuilder, GUser}

object JiraTaskBuilder {

  def buildJiraTask(assignee: Option[GUser] = None): GTask = {
    val task = GTaskBuilder.withRandom(JiraField.summary)
    task.setValue(Assignee, assignee.getOrElse(null))
    task
  }

}
