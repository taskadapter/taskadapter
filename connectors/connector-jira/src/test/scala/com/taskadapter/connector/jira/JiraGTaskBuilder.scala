package com.taskadapter.connector.jira

import java.util.Date

import com.taskadapter.connector.definition.TaskId
import com.taskadapter.model.GTask

import scala.util.Random

object JiraGTaskBuilder {
  def withPriority(value: Object): GTask = {
    val task = new GTask
    task.setValue(JiraField.summary, "task " + new Date().getTime)
    task.setValue(JiraField.priority, value)
    task
  }

  def withType(value: Object): GTask = {
    val task = new GTask
    task.setValue(JiraField.summary, "task " + new Date().getTime)
    task.setValue(JiraField.taskType, value)
    task
  }

  def withSummary(value: String = Random.nextDouble().toString): GTask = {
    val task = new GTask
    task.setValue(JiraField.summary, value)
    task
  }

  def withDescription(): GTask = {
    val task = new GTask
    task.setValue(JiraField.description, "description " + new Date().getTime)
    task
  }

  def getTwo(): List[GTask] = {
    List(withSummary(), withSummary())
  }
}

class JiraGTaskBuilder(summary: String = "task " + new Date().getTime) {
  val task = new GTask
  // summary is pretty much always required
  task.setValue(JiraField.summary, summary)


  def withDescription(): JiraGTaskBuilder = {
    task.setValue(JiraField.description, "description " + new Date().getTime)
    this
  }

  def withPriority(value: Integer): JiraGTaskBuilder = {
    task.setValue(JiraField.priority, value)
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

  def withType(value: Object): JiraGTaskBuilder = {
    task.setValue(JiraField.taskType, value)
    this
  }

  def build(): GTask = {
    task
  }
}
