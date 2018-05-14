package com.taskadapter.connector.redmine

import com.taskadapter.model.{GTask, Summary}

import scala.util.Random

object RedmineGTaskBuilder {
  def withSummary(value: String = Random.nextDouble().toString): GTask = {
    val task = new GTask
    task.setValue(Summary, value)
    task
  }

  def getTwo(): List[GTask] = {
    List(withSummary(), withSummary())
  }
}
