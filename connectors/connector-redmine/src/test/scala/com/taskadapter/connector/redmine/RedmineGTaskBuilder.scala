package com.taskadapter.connector.redmine

import com.taskadapter.model.GTask

import scala.util.Random

object RedmineGTaskBuilder {
  def withSummary(value: String = Random.nextDouble().toString): GTask = {
    val task = new GTask
    task.setValue(RedmineField.summary, value)
    task
  }

  def getTwo(): List[GTask] = {
    List(withSummary(), withSummary())
  }
}
