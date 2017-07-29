package com.taskadapter.connector

object FieldRow {
  def apply(sourceField: Field, targetField: Field, defaultValueForEmpty: String) : FieldRow = {
    FieldRow(Some(sourceField), Some(targetField), defaultValueForEmpty)
  }
}

case class FieldRow(sourceField: Option[Field], targetField: Option[Field], defaultValueForEmpty: String)