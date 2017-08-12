package com.taskadapter.model

import java.util.Date

import com.taskadapter.connector.Field

class GTaskBuilder {
  val task = new GTask

  def withRandom(field: Field): GTaskBuilder = {
    withRandom(field.name)
  }

  def withRandom(field: String): GTaskBuilder = {
    val value = "value " + new Date().getTime
    task.setValue(field, value)
    this
  }

  def build(): GTask = {
    task
  }
}

