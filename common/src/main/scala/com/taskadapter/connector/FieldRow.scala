package com.taskadapter.connector

import com.taskadapter.model.Field

import java.util.Optional

object FieldRow {
  def apply[T](sourceField: Field[T], targetField: Field[T], defaultValueForEmpty: String) : FieldRow[T] = {
    FieldRow(Optional.ofNullable(sourceField), Optional.ofNullable(targetField), defaultValueForEmpty)
  }
}

case class FieldRow[T](sourceField: Optional[Field[T]], targetField: Optional[Field[T]], defaultValueForEmpty: String)