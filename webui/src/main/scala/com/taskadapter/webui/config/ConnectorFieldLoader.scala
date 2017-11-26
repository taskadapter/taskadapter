package com.taskadapter.webui.config

import java.util

import com.taskadapter.connector.Field
import scala.collection.JavaConverters._

class ConnectorFieldLoader(fields: util.List[Field]) {

  def getTypeForFieldName(fieldName: String): String = {
    fields.asScala.find(f => f.name.equals(fieldName)).map(_.typeName).getOrElse("Unknown")
  }
}
