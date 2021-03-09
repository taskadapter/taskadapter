package com.taskadapter.connector.github

import java.util

import com.taskadapter.connector.definition.TaskId
import com.taskadapter.model._
import org.eclipse.egit.github.core.Issue

object GithubToGTask {
  def toGTaskList(issues: util.List[Issue]): util.List[GTask] = {
    val tasks = new util.ArrayList[GTask]
    import scala.collection.JavaConversions._
    for (issue <- issues) {
      val task = toGtask(issue)
      tasks.add(task)
    }
    tasks
  }

  def toGtask(issue: Issue): GTask = {
    val task = new GTask
    val stringKey = Integer.toString(issue.getNumber)
    task.setId(java.lang.Long.parseLong(stringKey))
    task.setKey(stringKey)
    task.setSourceSystemId(new TaskId(issue.getId, stringKey))
    task.setValue(AllFields.summary, issue.getTitle)
    task.setValue(AllFields.description, issue.getBody)
    task.setValue(AllFields.createdOn, issue.getCreatedAt)
    task.setValue(AllFields.updatedOn, issue.getUpdatedAt)
    if (issue.getAssignee != null) {
      task.setValue(AllFields.assigneeFullName, issue.getAssignee.getName)
      task.setValue(AllFields.assigneeLoginName, issue.getAssignee.getLogin)
    }
    task
  }
}