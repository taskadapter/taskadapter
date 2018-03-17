package com.taskadapter.integrationtests

import com.taskadapter.connector.redmine.RedmineField
import com.taskadapter.model.GTask

import scala.util.Random

object RedmineTaskBuilder {
  def withSummary(value: String = Random.nextDouble().toString): GTask = {
    val task = new GTask
    task.setValue(RedmineField.summary, value)
    task
  }
}
