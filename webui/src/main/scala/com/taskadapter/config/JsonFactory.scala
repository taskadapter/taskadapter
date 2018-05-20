package com.taskadapter.config

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.taskadapter.connector.common.ConfigUtils
import com.taskadapter.connector.definition.FieldMapping
import com.taskadapter.model.{Assignee, Children, ClosedOn, Components, CreatedOn, CustomDate, CustomFloat, CustomSeqString, CustomString, Description, DoneRatio, DueDate, EstimatedTime, Field, Id, Key, ParentKey, Priority, Relations, Reporter, SourceSystemId, StartDate, Summary, TargetVersion, TaskStatus, TaskType, UpdatedOn}
import com.taskadapter.webui.config.DefaultValueResolver

import scala.util.parsing.json.JSON

object JsonFactory {
  val mapper = new ObjectMapper() with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)
  mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
  mapper.enableDefaultTyping()
  mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL)

  val gson = ConfigUtils.createDefaultGson

  def toString(mappings: Seq[FieldMapping[_]]): String = {
    val seq = mappings.map { m =>
      val defaultValueJsonString = if (m.defaultValue == null) null else gson.toJson(m.defaultValue)
      s"""{ "fieldInConnector1": ${toString(m.fieldInConnector1)},
          "fieldInConnector2": ${toString(m.fieldInConnector2)},
          "defaultValue" : $defaultValueJsonString,
          "selected": "${m.selected}"
           }"""
    }
    val asd = seq.mkString(",")
    s"[ $asd ]".filter(_ >= ' ')
  }


  def toString(f: Option[Field[_]]): String = {
    val seq = s"""{ "type" : "${f.get.getClass.getSimpleName}", "name": "${f.get.name}" } """
    seq
  }

  def fromJsonString(jsonString: String): Seq[FieldMapping[_]] = {
    val result = JSON.parseFull(jsonString).map {
      case json: List[Map[String, Any]] =>
        json.map(i => {
          val fieldInConnector1 = i("fieldInConnector1").asInstanceOf[Map[String, Any]]
          val fieldInConnector2 = i("fieldInConnector2").asInstanceOf[Map[String, Any]]
          val selected = java.lang.Boolean.parseBoolean(i("selected").toString)
          val default = i("defaultValue")

          val fakeClassWhyIsThisEvenNeededWTF = classOf[String]
          val field1 = fieldFromJson(fakeClassWhyIsThisEvenNeededWTF, fieldInConnector1)
          val field2 = fieldFromJson(fakeClassWhyIsThisEvenNeededWTF, fieldInConnector2)
          FieldMapping(field1, field2, selected,
            DefaultValueResolver.getTag(field1).deserValue(default).asInstanceOf[String])
        })
    }.get
    result
  }

  def fieldFromJson[T](clazz: Class[T], json: Map[String, Any]): Field[T] = {

    val fieldName = json("name").asInstanceOf[String]
    val gType = json("type")
    val result = gType match {
      case "Assignee$" => Assignee
      case "Children$" => Children
      case "Components$" => Components
      case "ClosedOn$" => ClosedOn
      case "CreatedOn$" => CreatedOn
      case "CustomString$" => CustomString(fieldName)
      case "CustomFloat$" => CustomFloat(fieldName)
      case "CustomDate$" => CustomDate(fieldName)
      case "CustomSeqString$" => CustomSeqString(fieldName)
      case "Description$" => Description
      case "DoneRatio" => DoneRatio
      case "DueDate$" => DueDate
      case "EstimatedTime$" => EstimatedTime
      case "Id$" => Id
      case "Key$" => Key
      case "ParentKey$" => ParentKey
      case "Priority$" => Priority
      case "Relations$" => Relations
      case "Reporter$" => Reporter
      case "SourceSystemId$" => SourceSystemId
      case "StartDate$" => StartDate
      case "Summary$" => Summary
      case "TaskStatus$" => TaskStatus
      case "TaskType$" => TaskType
      case "TargetVersion$" => TargetVersion
      case "UpdatedOn$" => UpdatedOn
      case _ => throw new RuntimeException(s"unknown type: $gType")
    }

    result.asInstanceOf[Field[T]]
  }

  //  def buildAllFieldMap() : Map[String, Class[Field[_]]] = {
  //    import scala.reflect.runtime.{universe => ru}
  //    val tpe = ru.typeOf[Field[_]]
  //    val clazz = tpe.typeSymbol.asClass
  //    // if you want to ensure the type is a sealed trait,
  //    // then you can use clazz.isSealed and clazz.isTrait
  //    val map = clazz.knownDirectSubclasses.map(c =>
  //      (c.getClass.getSimpleName -> c.getClass.asInstanceOf[Field[_]]))
  //    println(map)
  //    map.toMap
  //  }
}
