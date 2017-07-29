package com.taskadapter.webui.pages

import com.taskadapter.PluginManager
import com.taskadapter.webui.Page.message
import com.vaadin.ui.Button.ClickListener
import com.vaadin.ui._

import scala.collection.JavaConverters._

class NewConfigSelectSystem(pluginManager: PluginManager, selected: String => Unit) {
  val layout = new VerticalLayout()
  layout.addComponent(new Label(message("newConfig.selectSystem")))

  createSystemList(event => {
    val connectorId = event.getButton.getData.asInstanceOf[String]
    selected(connectorId)
  }
  )

  private def createSystemList(listener: ClickListener): Unit = {
    pluginManager.getPluginDescriptors.asScala.foreach { connector =>

      val system = new HorizontalLayout()
      system.setWidth(200, com.vaadin.server.Sizeable.Unit.PIXELS)
      system.setHeight(50, com.vaadin.server.Sizeable.Unit.PIXELS)
      system.addStyleName("connectorBoxOnNewConfigPage")
      system.addLayoutClickListener(_ => selected(connector.id))

      val label = new Label(connector.label)
      label.addStyleName("connectorLabelOnNewConfigPage")

      system.addComponent(label)
      system.setComponentAlignment(label, Alignment.MIDDLE_CENTER)

      layout.addComponent(system)

      layout.setComponentAlignment(system, Alignment.MIDDLE_CENTER)
    }
  }

  def ui = layout
}
