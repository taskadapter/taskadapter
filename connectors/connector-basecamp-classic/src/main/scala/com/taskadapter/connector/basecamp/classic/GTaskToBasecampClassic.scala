package com.taskadapter.connector.basecamp.classic

import java.util.Date

import com.taskadapter.connector.basecamp.BasecampTaskWrapper
import com.taskadapter.connector.common.data.ConnectorConverter
import com.taskadapter.model._
import org.w3c.dom.{Document, Element}

import scala.collection.JavaConverters._

class GTaskToBasecampClassic(users: Seq[GUser]) extends ConnectorConverter[GTask, BasecampTaskWrapper] {

  /**
    * Convert a task from source to target format.
    *
    * @param source source object to convert.
    * @return converted object
    */
  override def convert(source: GTask): BasecampTaskWrapper = {

    val d = BasecampUtils.newXDoc
    val root = d.createElement("todo-item")
    d.appendChild(root)

    source.getFields.asScala.foreach { x =>
      processField(d, root, x._1, x._2)
    }
    val str = BasecampUtils.stringify(d)

    BasecampTaskWrapper(source.getKey, str, source.getValue(DoneRatio))
  }

  def processField(d: Document, e: Element, field: Field[_], value: Any): Unit = {
    field match {
      case BasecampClassicField.content =>
        val stringValue = value.asInstanceOf[String]
        XmlUtils.setString(d, e, "content", stringValue)
      case DoneRatio =>
        val booleanValue: Boolean = if (value == null) false
        else if (value.asInstanceOf[Float] >= 100) true
        else false

        val compl = d.createElement("completed")
        compl.setAttribute("type", "boolean")
        compl.appendChild(d.createTextNode(booleanValue.toString))
        e.appendChild(compl)

      case DueDate =>
        XmlUtils.setLong(d, e, "due-at", value.asInstanceOf[Date])
      case AssigneeFullName =>
        writeAssignee(d, e, value.asInstanceOf[String])

      case _ => // ignore unknown fields
    }
  }

  def writeAssignee(d: Document, root: Element, fullName: String): Unit = {
    val elt = d.createElement("responsible-party")
    root.appendChild(elt)

    val resolvedAssignee = users.find(_.displayName == fullName).orNull
    if (resolvedAssignee == null || resolvedAssignee.id == null) elt.setAttribute("nil", "true")
    else elt.appendChild(d.createTextNode(resolvedAssignee.id.toString))
  }
}

