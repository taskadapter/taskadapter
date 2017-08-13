package com.taskadapter.model

import com.taskadapter.connector.{Field, FieldRow}

object FieldRowBuilder {
  def rows(field: Field*): Seq[FieldRow] = {
    field.map(f => FieldRow(f, f, ""))
  }
}