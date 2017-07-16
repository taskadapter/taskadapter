package com.taskadapter.connector.redmine

import java.util

import com.taskadapter.connector.{Field, FieldRow}

import scala.collection.JavaConverters._

object RedmineFieldBuilder {
  def withField(typeForField: String, name: String, defaultValue: String = ""): util.List[FieldRow] = {
    List(
      FieldRow(RedmineField.summary, RedmineField.summary, ""),
      FieldRow(Field(typeForField, name), Field(typeForField, name), defaultValue)
    ).asJava
  }

  def getDefault(): List[FieldRow] = {
    List(
      FieldRow(RedmineField.summary, RedmineField.summary, ""),
    )
  }
}
