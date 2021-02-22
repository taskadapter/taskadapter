package com.taskadapter.webui.config

import com.taskadapter.model.{CustomString, Field}
import scala.collection.JavaConverters._

class ConnectorFieldLoader(fields: java.util.List[Field[_]]) {

  def getTypeForFieldName(fieldName: String): Field[_] = {
    fields.asScala.find(f => f.name.equals(fieldName)).getOrElse(CustomString(fieldName))
  }
}
