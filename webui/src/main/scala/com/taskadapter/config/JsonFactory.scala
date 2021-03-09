package com.taskadapter.config

import com.taskadapter.connector.common.ConfigUtils
import com.taskadapter.common.ui.FieldMapping
import com.taskadapter.model.{AssigneeFullName, AssigneeLoginName, Children, ClosedOn, Components, CreatedOn, CustomDate, CustomFloat, CustomListString, CustomString, Description, DoneRatio, DueDate, EstimatedTime, Field, Id, Key, ParentKey, Priority, Relations, ReporterFullName, ReporterLoginName, SourceSystemId, StartDate, Summary, TargetVersion, TaskStatus, TaskType, UpdatedOn}

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
    var gType = json("type").toString
    // temporary adapter to recognize legacy (pre- March-2021) fields ending with "$" (leftover from Scala)
    gType = gType.replace("$", "")
    val result = gType match {
      case "AssigneeLoginName" => new AssigneeLoginName
      case "AssigneeFullName" => new AssigneeFullName
      case "Children" => new Children
      case "Components" => new Components
      case "ClosedOn" => new ClosedOn
      case "CreatedOn" => new CreatedOn
      case "CustomString" => new CustomString(fieldName)
      case "CustomFloat" => new CustomFloat(fieldName)
      case "CustomDate" => new CustomDate(fieldName)
      case "CustomSeqString" => new CustomListString(fieldName)
      case "Description" => new Description
      case "DoneRatio" => new DoneRatio
      case "DueDate" => new DueDate
      case "EstimatedTime" => new EstimatedTime
//      case "SpentTime" => SpentTime
      case "Id" => new Id
      case "Key" => new Key
      case "ParentKey" => new ParentKey
      case "Priority" => new Priority
      case "Relations" => new Relations
      case "ReporterFullName" => new ReporterFullName
      case "ReporterLoginName" => new ReporterLoginName
      case "SourceSystemId" => new SourceSystemId
      case "StartDate" => new StartDate
      case "Summary" => new Summary
      case "TaskStatus" => new TaskStatus
      case "TaskType" => new TaskType
      case "TargetVersion" => new TargetVersion
      case "UpdatedOn" => new UpdatedOn
      case _ => throw new RuntimeException(s"unknown type: $gType")
    }
    Optional.of(result.asInstanceOf[Field[T]])
  }
}
