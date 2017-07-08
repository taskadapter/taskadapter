package com.taskadapter.model

import java.util.Date

class GTaskBuilder {
  val task = new GTask

  def withRandom(field: String): GTaskBuilder = {
    val value = "value " + new Date().getTime
    task.setValue(field, value)
    this
  }

  def build(): GTask = {
    task
  }
}

