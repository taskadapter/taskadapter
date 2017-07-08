package com.taskadapter.connector.redmine

import com.taskadapter.connector.FieldRow

import scala.collection.JavaConverters._

object ABC {
  def buildSourceConnectorFieldNames(rows: java.util.List[FieldRow]): java.util.List[String] = {
    rows.asScala.map(r => r.nameInSource).asJava
  }

  def buildSourceConnectorDefaultValues(rows: java.util.List[FieldRow]): java.util.Map[String, String] = {
    rows.asScala.map(r => r.nameInSource -> r.defaultValueForEmpty).toMap.asJava
  }

  def targetConnectorFieldNames(rows: java.util.List[FieldRow]): java.util.List[String] = {
    rows.asScala.map(r => r.nameInTarget).asJava
  }

  def targetConnectorDefaultValues(rows: java.util.List[FieldRow]): java.util.Map[String, String] = {
    rows.asScala.map(r => r.nameInTarget -> r.defaultValueForEmpty).toMap.asJava
  }

}
