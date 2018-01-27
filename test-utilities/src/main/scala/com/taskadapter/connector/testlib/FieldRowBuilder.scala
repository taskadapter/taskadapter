package com.taskadapter.connector.testlib

import com.taskadapter.connector.{Field, FieldRow}

object FieldRowBuilder {
  def rows(field: Field*): Seq[FieldRow] = {
    field.map(f => FieldRow(f, f, ""))
  }
}
