package com.taskadapter.connector.jira

import java.util.Date

import com.taskadapter.connector.definition.TaskId
import com.taskadapter.model._

object JiraGTaskBuilder {
  def withType(value: String): GTask = {
    val task = new GTask
    task.setValue(Summary, "task " + new Date().getTime)
    task.setValue(TaskType, value)
    task
  }
}

class JiraGTaskBuilder(summary: String = "task " + new Date().getTime) {
  val task = new GTask
  // summary is pretty much always required
  task.setValue(Summary, summary)


  def withDescription(): JiraGTaskBuilder = {
    task.setValue(Description, "description " + new Date().getTime)
    this
  }

  def withPriority(value: Integer): JiraGTaskBuilder = {
    task.setValue(Priority, value)
    this
  }

  def withParentId(value: TaskId): JiraGTaskBuilder = {
    task.setParentIdentity(value)
    this
  }

  def withId(value: Long): JiraGTaskBuilder = {
    task.setId(value)
    this
  }

  def withType(value: String): JiraGTaskBuilder = {
    task.setValue(TaskType, value)
    this
  }

  def build(): GTask = {
    task
  }
}
