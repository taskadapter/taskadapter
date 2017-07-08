package com.taskadapter.connector.redmine

import java.util

import com.taskadapter.connector.FieldRow

import scala.collection.JavaConverters._

object RedmineFieldBuilder {
  def withField(name: String, defaultValue: String = ""): util.List[FieldRow] = {
    List(
      FieldRow(true, RedmineField.summary, RedmineField.summary, ""),
      FieldRow(true, name, name, defaultValue)
    ).asJava
  }

  def getDefault(): util.List[FieldRow] = {
    List(
      FieldRow(true, RedmineField.summary, RedmineField.summary, ""),
    ).asJava
  }
}
