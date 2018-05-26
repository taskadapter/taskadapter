package com.taskadapter.connector.testlib

import com.taskadapter.connector.FieldRow
import com.taskadapter.model.Field

object FieldRowBuilder {

  def rows(field: Seq[Field[_]]): Seq[FieldRow[_]] = {
    field.map(f => build(f))
  }

  def build[T](f: Field[T]): FieldRow[T] = new FieldRow[T](Option(f), Option(f), null.asInstanceOf[String])
}
