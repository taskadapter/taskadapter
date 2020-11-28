package com.taskadapter.web.configeditor.server

import java.util

import com.taskadapter.connector.definition.exceptions.{BadConfigException, ConnectorException}
import com.taskadapter.model.{NamedKeyedObject, NamedKeyedObjectImpl}
import com.taskadapter.web.ExceptionFormatter
import com.taskadapter.web.configeditor.{EditorUtil, ListSelectionDialog}
import com.vaadin.ui.{Button, Notification, UI}
import org.slf4j.LoggerFactory

object ButtonFactory {
  private val logger = LoggerFactory.getLogger(ButtonFactory.getClass)

  def createLookupButton(buttonLabel: String, description: String, windowTitle: String, listTitle: String,
                         loadProjects: () => Seq[_ <: NamedKeyedObject],
                         errorFormatter: ExceptionFormatter,
                         selectionListener: NamedKeyedObject => Unit): Button = {
    val button = new Button(buttonLabel)
    button.setDescription(description)
    val listener = new LookupResultListenerScala() {
      override def notifyDone(objects: Seq[_ <: NamedKeyedObject]): Unit = {
        if (objects.nonEmpty) showValues(objects)
      }

      def showValues(objects: Seq[_ <: NamedKeyedObject]): Unit = {
        val map = new util.TreeMap[String, String]
        for (o <- objects) {
          map.put(o.getName, o.getKey)
        }
        showList(windowTitle, listTitle, map.keySet, (value: String) => {
          val key = map.get(value)
          selectionListener.apply(NamedKeyedObjectImpl(key, value))
        })
      }
    }
    button.addClickListener(_ => {
        try {
          val objects = loadProjects()
          if (objects.isEmpty) Notification.show("No objects", "No objects have been found", Notification.Type.HUMANIZED_MESSAGE)
          listener.notifyDone(objects)
        } catch {
          case e: BadConfigException =>
            logger.error(e.toString)
            Notification.show("", errorFormatter.formatError(e), Notification.Type.HUMANIZED_MESSAGE)
          case e: ConnectorException =>
            logger.error(e.toString)
            Notification.show("", errorFormatter.formatError(e), Notification.Type.HUMANIZED_MESSAGE)
          case e: Exception =>
            logger.error(e.toString)
            EditorUtil.show("Something went wrong", e)
        }
    })
    button
  }

  private def showList(windowTitle: String, listTitle: String, items: util.Collection[String],
                       valueListener: EditorUtil.ValueListener): Unit = {
    val newWindow = new ListSelectionDialog(windowTitle, listTitle, items, valueListener)
    newWindow.center()
    newWindow.setModal(true)
    UI.getCurrent.addWindow(newWindow)
    newWindow.focus()
  }

}
