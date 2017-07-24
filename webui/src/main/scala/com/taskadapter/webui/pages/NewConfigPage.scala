package com.taskadapter.webui.pages

import com.taskadapter.PluginManager
import com.taskadapter.config.StorageException
import com.taskadapter.connector.definition.WebServerInfo
import com.taskadapter.web.configeditor.server.ServerPanel
import com.taskadapter.web.uiapi.UISyncConfig
import com.taskadapter.webui.ConfigOperations
import com.taskadapter.webui.Page.message
import com.vaadin.data.Property
import com.vaadin.data.Property.ValueChangeListener
import com.vaadin.data.util.MethodProperty
import com.vaadin.ui._

trait Callback {
  /**
    * This method is called after new config was created.
    *
    * @param config created config.
    */
  def configCreated(config: UISyncConfig): Unit
}

object NewConfigPage {

  /**
    * Render "New Config" page
    */
  def render(pluginManager: PluginManager, ops: ConfigOperations, callback: Callback): Component = new NewConfigPage(pluginManager, ops, callback).panel
}

class NewConfigPage(pluginManager: PluginManager, configOps: ConfigOperations, callback: Callback) {
  val connector1Info = new WebServerInfo
  val connector2Info = new WebServerInfo

  var server1Panel: Option[ServerPanel] = None
  var server2Panel: Option[ServerPanel] = None

  val panel = new Panel(message("createConfigPage.createNewConfig"))
  panel.setWidth("600px")
  val grid = new GridLayout(2, 5)

  grid.setSpacing(true)
  grid.setMargin(true)
  panel.setContent(grid)

  val connector1 = createSystemListSelector(connector1Info, message("createConfigPage.system1"), pluginManager,
    event => if (server1Panel.isEmpty) {
      server1Panel = Some(createConnectorSetupFragment(connector1Info, event.getProperty.getValue.asInstanceOf[String]))
      serverInfoLayout.addComponent(server1Panel.get, 0,0)
    }
  )

  grid.addComponent(connector1, 0, 0)
  val connector2 = createSystemListSelector(connector2Info, message("createConfigPage.system2"), pluginManager,
    event => if (server2Panel.isEmpty) {
      server2Panel = Some(createConnectorSetupFragment(connector2Info, event.getProperty.getValue.asInstanceOf[String]))
      serverInfoLayout.addComponent(server2Panel.get, 1,0)
    }
  )
  grid.addComponent(connector2, 1, 0)
  val descriptionTextField = new TextField(message("createConfigPage.description"))
  descriptionTextField.setInputPrompt(message("createConfigPage.optional"))
  descriptionTextField.setWidth("100%")
  grid.addComponent(descriptionTextField, 0, 1, 1, 1)
  grid.setComponentAlignment(descriptionTextField, Alignment.MIDDLE_CENTER)
  // empty label by default
  val errorMessageLabel = new Label
  errorMessageLabel.addStyleName("error-message-label")
  val saveButton = new Button(message("createConfigPage.create"))
  saveButton.addClickListener(_ => saveClicked())
  grid.addComponent(errorMessageLabel, 0, 2, 1, 2)
  grid.addComponent(saveButton, 1, 3)
  grid.setComponentAlignment(saveButton, Alignment.MIDDLE_RIGHT)

  val serverInfoLayout = new GridLayout(2,1)
  grid.addComponent(serverInfoLayout, 0, 4, 1, 4)

  private def saveClicked() = {
    validate() match {
      case None => errorMessageLabel.setValue("")
        try {
          val config = save()
          callback.configCreated(config)
        } catch {
          case e: StorageException =>
            errorMessageLabel.setValue(message("createConfigPage.failedToSave"))
        }
      case Some(error) => errorMessageLabel.setValue(error)
    }
  }

  /**
    * @return None if no error was found
    */
  private def validate(): Option[String] = {
    if (connector1.getValue == null) {
      return Some(message("createConfigPage.pleaseSelectSystem1"))
    }

    val info1Error = connector1Info.validate()
    if (!info1Error.isEmpty) {
      return Some(info1Error)
    }

    if (connector2.getValue == null) {
      return Some(message("createConfigPage.pleaseSelectSystem2"))
    }
    val info2Error = connector2Info.validate()
    if (!info2Error.isEmpty) {
      return Some(info2Error)
    }
    None
  }

  @throws[StorageException]
  private def save(): UISyncConfig = {
    val descriptionString = descriptionTextField.getValue
    val id1 = connector1.getValue.asInstanceOf[String]
    val id2 = connector2.getValue.asInstanceOf[String]
    configOps.createNewConfig(descriptionString, id1, id2, connector1Info, connector2Info)
  }

  private def createSystemListSelector(connectorInfo: WebServerInfo, title: String, plugins: PluginManager,
                                       valueChangeListener: ValueChangeListener) = {
    val res = new ListSelect(title)
    res.setRequired(true)
    res.setNullSelectionAllowed(false)
    res.addValueChangeListener(valueChangeListener)
    val connectors = plugins.getPluginDescriptors
    while ( {
      connectors.hasNext
    }) {
      val connector = connectors.next
      res.addItem(connector.id)
      res.setItemCaption(connector.id, connector.label)
    }
    res.setRows(res.size)
    res
  }

  private def createConnectorSetupFragment(connectorInfo: WebServerInfo, connectorId: String): ServerPanel = {
    val caption = message("createConfigPage.serverPanelCaptionFor") + s" $connectorId"
    val serverPanel = new ServerPanel(caption,
      new MethodProperty[String](connectorInfo, "label"),
      new MethodProperty[String](connectorInfo, "host"),
      new MethodProperty[String](connectorInfo, "userName"),
      new MethodProperty[String](connectorInfo, "password"))
    serverPanel
  }
}
