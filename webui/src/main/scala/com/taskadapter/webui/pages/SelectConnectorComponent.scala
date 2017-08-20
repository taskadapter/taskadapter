package com.taskadapter.webui.pages

import com.taskadapter.PluginManager
import com.taskadapter.webui.Page.message
import com.vaadin.ui.Button.ClickListener
import com.vaadin.ui._

import scala.collection.JavaConverters._

class SelectConnectorWizardStep(pluginManager: PluginManager, next: String => Unit) extends WizardStep[String] {
  var result: String = ""

  override def getResult: String = result

  override def ui(config: Any) = new SelectConnectorComponent(pluginManager, (connectorId) => {
    result = connectorId
    next(connectorId)
  }).layout
}

class SelectConnectorComponent(pluginManager: PluginManager, next: String => Unit) {
  val layout = new VerticalLayout()
  layout.setSpacing(true)
  layout.setMargin(true)
  layout.addComponent(new Label(message("newConfig.selectSystem")))

  createSystemList(event => {
    val connectorId = event.getButton.getData.asInstanceOf[String]
    next(connectorId)
  })

  private def createSystemList(listener: ClickListener): Unit = {
    pluginManager.getPluginDescriptors.asScala.foreach { connector =>

      val systemButton = new Button(connector.label)
      systemButton.setWidth("200px")
      systemButton.addClickListener(_ => next(connector.id))
      layout.addComponent(systemButton)
      layout.setComponentAlignment(systemButton, Alignment.MIDDLE_CENTER)
    }
  }

}
