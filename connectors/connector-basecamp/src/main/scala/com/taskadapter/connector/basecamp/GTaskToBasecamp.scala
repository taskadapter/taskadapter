package com.taskadapter.connector.basecamp

import java.io.StringWriter
import java.util.Date

import com.taskadapter.connector.common.data.ConnectorConverter
import com.taskadapter.connector.definition.exception.FieldConversionException
import com.taskadapter.model._
import org.json.JSONWriter

import scala.collection.JavaConverters._

class GTaskToBasecamp(users: Seq[GUser]) extends ConnectorConverter[GTask, BasecampTaskWrapper] {

  /**
    * Convert a task from source to target format.
    *
    * @param source source object to convert.
    * @return converted object
    */
  override def convert(source: GTask): BasecampTaskWrapper = {

    val sw = new StringWriter

    try {
      val writer = new JSONWriter(sw)
      writer.`object`
      source.getFields.asScala.foreach { x =>
        val field = x._1
        val value = x._2
        try {
          processField(writer, field, value)
        } catch {
          case _: Exception => throw FieldConversionException(BasecampConnector.ID, field, value)
        }
      }
      writer.endObject
    } finally sw.close()

    BasecampTaskWrapper(source.getKey, sw.toString, source.getValue(DoneRatio))
  }

  def processField(writer: JSONWriter, field: Field[_], value: Any): Unit = {
    field match {
      case BasecampField.content =>
        val stringValue = value.asInstanceOf[String]
        JsonUtils.writeOpt(writer, "content", stringValue)
      case DoneRatio =>
        val booleanValue: Boolean = if (value == null) false
        else if (value.asInstanceOf[Float] >= 100) true
        else false
        JsonUtils.writeOpt(writer, "completed", booleanValue)
      case DueDate =>
        JsonUtils.writeShort(writer, "due_at", value.asInstanceOf[Date])
      case AssigneeFullName => writeAssignee(writer, value.asInstanceOf[String])

      case _ => // ignore unknown fields
    }

    def writeAssignee(writer: JSONWriter, fullName: String): Unit = {
      val field = "assignee"
      if (fullName == null) {
        writer.key(field).value(null)
        return
      }
      val resolvedAssignee = users.find(_.displayName == fullName).orNull
      if (resolvedAssignee == null || resolvedAssignee.id == null) return
      writer.key(field).`object`.key("type").value("Person")
      writer.key("id").value(resolvedAssignee.id.intValue)
      writer.endObject
    }

  }
}

