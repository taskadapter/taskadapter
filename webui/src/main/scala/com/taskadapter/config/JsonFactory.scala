package com.taskadapter.config

import com.taskadapter.connector.common.ConfigUtils
import com.taskadapter.connector.definition.FieldMapping
import com.taskadapter.model.{AssigneeFullName, AssigneeLoginName, Children, ClosedOn, Components, CreatedOn, CustomDate, CustomFloat, CustomSeqString, CustomString, Description, DoneRatio, DueDate, EstimatedTime, Field, Id, Key, ParentKey, Priority, Relations, ReporterFullName, ReporterLoginName, SourceSystemId, StartDate, Summary, TargetVersion, TaskStatus, TaskType, UpdatedOn}

import scala.util.parsing.json.JSON

object JsonFactory {
  val gson = ConfigUtils.createDefaultGson

  def toString(mappings: Seq[FieldMapping[_]]): String = {
    val seq = mappings.map { m =>
      val defaultValueJsonString = if (m.defaultValue == null) null else gson.toJson(m.defaultValue)
      s"""{ "fieldInConnector1": ${toString(m.fieldInConnector1)}, "fieldInConnector2": ${toString(m.fieldInConnector2)},
"defaultValue" : $defaultValueJsonString, "selected": "${m.selected}" }"""
    }
    val asd = seq.mkString(",")
    s"[ $asd ]".filter(_ >= ' ')
  }


  private def toString(f: Option[Field[_]]): String = {
    if (f.isEmpty) {
      "null"
    } else {
      s"""{ "type" : "${f.get.getClass.getSimpleName}", "name": "${f.get.name}" } """
    }
  }

  def fromJsonString(jsonString: String): Seq[FieldMapping[_]] = {
    val result = JSON.parseFull(jsonString).map {
      case json: List[Map[String, Any]] =>
        json.map(i => {
          val fieldInConnector1 = i("fieldInConnector1").asInstanceOf[Map[String, Any]]
          val fieldInConnector2 = i("fieldInConnector2").asInstanceOf[Map[String, Any]]
          val selected = java.lang.Boolean.parseBoolean(i("selected").toString)
          val default = i("defaultValue")

          val field1 = fieldFromJson(fieldInConnector1)
          val field2 = fieldFromJson(fieldInConnector2)
          FieldMapping(field1.asInstanceOf[Option[Field[Any]]],
            field2.asInstanceOf[Option[Field[Any]]],
            selected,
            default.asInstanceOf[String])
        })
    }.get
    result
  }

  private def fieldFromJson[T](json: Map[String, Any]): Option[Field[T]] = {
    if (json == null) {
      return None
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
    Some(result.asInstanceOf[Field[T]])
  }
}
