package com.taskadapter.webui.config

import com.taskadapter.model.Field

class ConnectorFieldLoader(fields: Seq[Field[_]]) {

  def getTypeForFieldName(fieldName: String): Field[_] = {
    fields.find(f => f.name.equals(fieldName)).get
  }
}
