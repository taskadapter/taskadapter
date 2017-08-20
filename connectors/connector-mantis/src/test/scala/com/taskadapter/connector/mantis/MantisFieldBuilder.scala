package com.taskadapter.connector.mantis

import com.taskadapter.connector.{Field, FieldRow}

object MantisFieldBuilder {

  def withField(typeForField: String, name: String, defaultValue: String = ""): Seq[FieldRow] = {
    List(
      FieldRow(MantisField.summary, MantisField.summary, ""),
      FieldRow(Field(typeForField, name), Field(typeForField, name), defaultValue)
    )
  }

  def getDefault(): List[FieldRow] = {
    List(
      FieldRow(MantisField.summary, MantisField.summary, ""),
      FieldRow(MantisField.description, MantisField.description, "-"),
    )
  }
}

