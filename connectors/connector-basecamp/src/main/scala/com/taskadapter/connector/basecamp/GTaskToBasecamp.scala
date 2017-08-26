package com.taskadapter.connector.basecamp

import java.io.StringWriter
import java.util.Date

import com.taskadapter.connector.common.data.ConnectorConverter
import com.taskadapter.model.{GTask, GUser}
import org.json.JSONWriter

import scala.collection.JavaConverters._

class GTaskToBasecamp(resolver: UserResolver) extends ConnectorConverter[GTask, BasecampTaskWrapper] {

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
        processField(writer, x._1, x._2)
      }
      writer.endObject
    } finally sw.close()

    BasecampTaskWrapper(source.getKey, sw.toString, source.getValue(BasecampField.doneRatio).asInstanceOf[Float])
  }

  def processField(writer: JSONWriter, fieldName: String, value: Any): Unit = {
    fieldName match {
      case BasecampField.content.name =>
        val stringValue = value.asInstanceOf[String]
        JsonUtils.writeOpt(writer, "content", stringValue)
      case BasecampField.doneRatio.name =>
        val booleanValue: Boolean = if (value == null) false
        else if (value.asInstanceOf[Float] >= 100) true
        else false
        JsonUtils.writeOpt(writer, "completed", booleanValue)
      case BasecampField.dueDate.name =>
        JsonUtils.writeShort(writer, "due_at", value.asInstanceOf[Date])
      case BasecampField.assignee.name => writeAssignee(writer, value.asInstanceOf[GUser])

      case _ => // ignore unknown fields
    }

    def writeAssignee(writer: JSONWriter, assignee: GUser): Unit = {
      val field = "assignee"
      if (assignee == null) {
        writer.key(field).value(null)
        return
      }
      val resolvedAssignee = resolver.resolveUser(assignee)
      if (resolvedAssignee == null || resolvedAssignee.getId == null) return
      writer.key(field).`object`.key("type").value("Person")
      writer.key("id").value(resolvedAssignee.getId.intValue)
      writer.endObject
    }

  }
}

