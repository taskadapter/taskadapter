package com.taskadapter.connector.basecamp

import java.io.StringWriter
import java.util.Date

import com.taskadapter.connector.common.data.ConnectorConverter
import com.taskadapter.model.GTask
import org.json.JSONWriter

import scala.collection.JavaConverters._

class GTaskToBasecamp(users: UserResolver) extends ConnectorConverter[GTask, BasecampTaskWrapper] {

  /**
    * Convert a task from source to target format.
    *
    * @param source source object to convert.
    * @return converted object
    */
  override def convert(source: GTask) = {

    val sw = new StringWriter

    try {
      val writer = new JSONWriter(sw)
      writer.`object`
      source.getFields.asScala.foreach { x =>
        processField(writer, x._1, x._2)
      }

      //      writeAssignee(writer, ctx, users, task.getAssignee)
      writer.endObject
    } finally sw.close()

    BasecampTaskWrapper(source.getKey, sw.toString, source.getValue(BasecampField.doneRatio).asInstanceOf[Float])
  }

  def processField(writer: JSONWriter, fieldName: String, value: Any): Unit = {
    fieldName match {
      case BasecampField.description.name =>
        val stringValue = value.asInstanceOf[String]
        JsonUtils.writeOpt(writer, "content", stringValue)
      case BasecampField.doneRatio.name =>
        val booleanValue: Boolean = if (value == null) false
        else if (value.asInstanceOf[Float] >= 100) true
        else false
        JsonUtils.writeOpt(writer, "completed", booleanValue)
      case BasecampField.dueDate.name =>
        JsonUtils.writeShort(writer, "due_at", value.asInstanceOf[Date])

      case _ => // ignore unknown fields
    }

    /*def writeAssignee(writer: JSONWriter, resolver: UserResolver, assignee: GUser): Unit ={
      val field = "assignee"
      if (field == null) return
      if (assignee == null) {
        writer.key(field).value(null)
        return
      }
      assignee = resolver.resolveUser(assignee)
      if (assignee == null || assignee.getId == null) return
      writer.key(field).`object`.key("type").value("Person")
      writer.key("id").value(assignee.getId.intValue)
      writer.endObject
    }
  }*/
  }
}

