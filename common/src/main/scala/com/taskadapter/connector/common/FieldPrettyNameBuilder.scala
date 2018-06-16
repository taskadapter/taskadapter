package com.taskadapter.connector.common

import com.taskadapter.model.Field

object FieldPrettyNameBuilder {
  def getPrettyFieldName(f: Field[_]): String = {
    if (f.getClass.getSimpleName.startsWith("Custom")) {
      f.toString
    } else {
      f.getClass.getSimpleName.replace("$", "")
    }
  }
}
