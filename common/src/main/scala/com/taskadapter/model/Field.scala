package com.taskadapter.model

import java.util
import java.util.Date

import com.taskadapter.connector.definition.TaskId

abstract sealed class Field[T](clazz: Class[T], val name: String)

object Field {
  def apply(fieldName: String): Field[String] = {
    CustomString(fieldName)
  }
}

object Assignee extends Field[GUser](classOf[GUser], "Assignee")

object Components extends Field[Seq[String]](classOf[Seq[String]], "Components")

object Reporter extends Field[GUser](classOf[GUser], "Reporter")

object ClosedOn extends Field[Date](classOf[Date], "ClosedOn")

object CreatedOn extends Field[Date](classOf[Date], "CreatedOn")

object UpdatedOn extends Field[Date](classOf[Date], "UpdatedOn")

object EstimatedTime extends Field[Float](classOf[Float], "EstimatedTime")

object Description extends Field[String](classOf[String], "Description")

object DoneRatio extends Field[Float](classOf[Float], "DoneRatio")

object DueDate extends Field[Date](classOf[Date], "DueDate")

object StartDate extends Field[Date](classOf[Date], "StartDate")

object Id extends Field[java.lang.Long](classOf[java.lang.Long], "Id")

object SourceSystemId extends Field[TaskId](classOf[TaskId], "SourceSystemId")

object Key extends Field[String](classOf[String], "Key")

object ParentKey extends Field[TaskId](classOf[TaskId], "ParentKey")

object Priority extends Field[Int](classOf[Int], "Priority")


object Summary extends Field[String](classOf[String], "Summary")

object Children extends Field[util.List[GTask]](classOf[util.List[GTask]], "Children")

object Relations extends Field[util.List[GRelation]](classOf[util.List[GRelation]], "Relations")

object TaskType extends Field[String](classOf[String], "TaskType")

object TaskStatus extends Field[String](classOf[String], "Status")

object TargetVersion extends Field[String](classOf[String], "TargetVersion")

case class CustomString(override val name: String) extends Field[String](classOf[String], name)

case class CustomSeqString(override val name: String) extends Field[Seq[String]](classOf[Seq[String]], name)
