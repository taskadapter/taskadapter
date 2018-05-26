package com.taskadapter.connector

import com.taskadapter.model.Field

object FieldRow {
  def apply[T](sourceField: Field[T], targetField: Field[T], defaultValueForEmpty: String) : FieldRow[T] = {
    FieldRow(Some(sourceField), Some(targetField), defaultValueForEmpty)
  }
}

case class FieldRow[T](sourceField: Option[Field[T]], targetField: Option[Field[T]], defaultValueForEmpty: String)