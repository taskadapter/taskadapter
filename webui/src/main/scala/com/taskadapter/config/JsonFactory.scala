package com.taskadapter.config

import com.taskadapter.connector.common.ConfigUtils
import com.taskadapter.common.ui.FieldMapping
import com.taskadapter.model.{AssigneeFullName, AssigneeLoginName, Children, ClosedOn, Components, CreatedOn, CustomDate, CustomFloat, CustomSeqString, CustomString, Description, DoneRatio, DueDate, EstimatedTime, Field, Id, Key, ParentKey, Priority, Relations, ReporterFullName, ReporterLoginName, SourceSystemId, StartDate, Summary, TargetVersion, TaskStatus, TaskType, UpdatedOn}

import java.util.Optional
import scala.util.parsing.json.JSON
import scala.collection.JavaConverters._

object JsonFactory {
  val gson = ConfigUtils.createDefaultGson

  def toString(mappings: java.util.List[FieldMapping[_]]): String = {
    val seq = mappings.asScala.map { m =>
      val defaultValueJsonString = if (m.getDefaultValue == null) null else gson.toJson(m.getDefaultValue)
      val field1Block = JsonFactoryJava.optionalToString(m.getFieldInConnector1)
      val field2Block = JsonFactoryJava.optionalToString(m.getFieldInConnector2)
      s"""{ "fieldInConnector1": $field1Block, "fieldInConnector2": $field2Block,
"defaultValue" : $defaultValueJsonString, "selected": "${m.isSelected}" }"""
    }
    val asd = seq.mkString(",")
    s"[ $asd ]".filter(_ >= ' ')
  }

  def fromJsonString(jsonString: String): Seq[FieldMapping[_]] = {
    val result = JSON.parseFull(jsonString).map {
      case json: List[Map[String, Any]] =>
        json.map(i => {
          val fieldInConnector1 = i("fieldInConnector1").asInstanceOf[Map[String, Any]]
          val fieldInConnector2 = i("fieldInConnector2").asInstanceOf[Map[String, Any]]
          val selected = java.lang.Boolean.parseBoolean(i("selected").toString)
          val default = i.getOrElse("defaultValue", null)

          val field1 = fieldFromJson(fieldInConnector1)
          val field2 = fieldFromJson(fieldInConnector2)
          new FieldMapping(field1.asInstanceOf[Optional[Field[Any]]],
            field2.asInstanceOf[Optional[Field[Any]]],
            selected,
            default.asInstanceOf[String])
        })
    }.get
    result
  }

  private def fieldFromJson[T](json: Map[String, Any]): Optional[Field[T]] = {
    if (json == null) {
      return Optional.empty()
    }
    val fieldName = json("name").asInstanceOf[String]
    val gType = json("type")
    val result = gType match {
      case "AssigneeLoginName$" => AssigneeLoginName
      case "AssigneeFullName$" => AssigneeFullName
      case "Children$" => Children
      case "Components$" => Components
      case "ClosedOn$" => ClosedOn
      case "CreatedOn$" => CreatedOn
      case "CustomString" => CustomString(fieldName)
      case "CustomFloat" => CustomFloat(fieldName)
      case "CustomDate" => CustomDate(fieldName)
      case "CustomSeqString" => CustomSeqString(fieldName)
      case "Description$" => Description
      case "DoneRatio$" => DoneRatio
      case "DueDate$" => DueDate
      case "EstimatedTime$" => EstimatedTime
//      case "SpentTime$" => SpentTime
      case "Id$" => Id
      case "Key$" => Key
      case "ParentKey$" => ParentKey
      case "Priority$" => Priority
      case "Relations$" => Relations
      case "ReporterFullName$" => ReporterFullName
      case "ReporterLoginName$" => ReporterLoginName
      case "SourceSystemId$" => SourceSystemId
      case "StartDate$" => StartDate
      case "Summary$" => Summary
      case "TaskStatus$" => TaskStatus
      case "TaskType$" => TaskType
      case "TargetVersion$" => TargetVersion
      case "UpdatedOn$" => UpdatedOn
      case _ => throw new RuntimeException(s"unknown type: $gType")
    }
    Optional.of(result.asInstanceOf[Field[T]])
  }
}
