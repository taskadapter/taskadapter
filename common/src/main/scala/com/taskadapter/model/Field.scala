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

object Children extends Field[util.List[GTask]]("Children")

object Components extends Field[Seq[String]]("Components")

object ClosedOn extends Field[Date]("ClosedOn")

object CreatedOn extends Field[Date]("CreatedOn")

case class CustomDate(override val name: String) extends Field[Date](name)

case class CustomFloat(override val name: String) extends Field[Float](name)

case class CustomSeqString(override val name: String) extends Field[Seq[String]](name)

case class CustomString(override val name: String) extends Field[String](name)

object Description extends Field[String]("Description")

object DoneRatio extends Field[Float]("DoneRatio")

object DueDate extends Field[Date]("DueDate")

object EstimatedTime extends Field[Float]("EstimatedTime")

object Id extends Field[java.lang.Long]("Id")

object Key extends Field[String]("Key")

object ParentKey extends Field[TaskId]("ParentKey")

object Priority extends Field[Int]("Priority")

object Relations extends Field[util.List[GRelation]]("Relations")

object Reporter extends Field[GUser]("Reporter")

object SourceSystemId extends Field[TaskId]("SourceSystemId")

object StartDate extends Field[Date]("StartDate")

object Summary extends Field[String]("Summary")

object TaskType extends Field[String]("TaskType")

object TaskStatus extends Field[String]("Status")

object TargetVersion extends Field[String]("TargetVersion")

object UpdatedOn extends Field[Date]("UpdatedOn")
