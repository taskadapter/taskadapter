package com.taskadapter.model

import java.util
import java.util.Date

import com.taskadapter.connector.definition.TaskId

sealed abstract class Field[T](val name: String)

object Field {
  def apply(fieldName: String): Field[String] = {
    CustomString(fieldName)
  }
}

object Assignee extends Field[GUser]("Assignee")

object Components extends Field[Seq[String]]("Components")

object Reporter extends Field[GUser]("Reporter")

object ClosedOn extends Field[Date]("ClosedOn")

object CreatedOn extends Field[Date]("CreatedOn")

object UpdatedOn extends Field[Date]("UpdatedOn")

object EstimatedTime extends Field[Float]("EstimatedTime")

object Description extends Field[String]("Description")

object DoneRatio extends Field[Float]("DoneRatio")

object DueDate extends Field[Date]("DueDate")

object StartDate extends Field[Date]("StartDate")

object Id extends Field[java.lang.Long]("Id")

object SourceSystemId extends Field[TaskId]("SourceSystemId")

object Key extends Field[String]("Key")

object ParentKey extends Field[TaskId]("ParentKey")

object Priority extends Field[Int]("Priority")


object Summary extends Field[String]("Summary")

object Children extends Field[util.List[GTask]]("Children")

object Relations extends Field[util.List[GRelation]]("Relations")

object TaskType extends Field[String]("TaskType")

object TaskStatus extends Field[String]("Status")

object TargetVersion extends Field[String]("TargetVersion")

case class CustomString(override val name: String) extends Field[String](name)

case class CustomDate(override val name: String) extends Field[Date](name)

case class CustomFloat(override val name: String) extends Field[Float](name)

case class CustomSeqString(override val name: String) extends Field[Seq[String]](name)
