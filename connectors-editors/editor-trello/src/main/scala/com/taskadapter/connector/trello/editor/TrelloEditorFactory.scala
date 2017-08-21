package com.taskadapter.connector.trello.editor

import java.util

import com.google.common.base.Strings
import com.taskadapter.connector.definition.WebConnectorSetup
import com.taskadapter.connector.definition.exceptions.{BadConfigException, LoginNameNotSpecifiedException, ServerURLNotSetException, UnsupportedConnectorOperation}
import com.taskadapter.connector.trello.{TrelloConfig, TrelloConnector}
import com.taskadapter.model.NamedKeyedObject
import com.taskadapter.web.callbacks.DataProvider
import com.taskadapter.web.configeditor.ProjectPanel
import com.taskadapter.web.configeditor.server.ServerPanelFactory
import com.taskadapter.web.data.Messages
import com.taskadapter.web.magic.Interfaces
import com.taskadapter.web.service.Sandbox
import com.taskadapter.web.{ConnectorSetupPanel, DroppingNotSupportedException, PluginEditorFactory}
import com.vaadin.data.util.ObjectProperty
import com.vaadin.server.Sizeable.Unit.PIXELS
import com.vaadin.ui.{ComponentContainer, VerticalLayout}

class TrelloEditorFactory extends PluginEditorFactory[TrelloConfig, WebConnectorSetup] {
  private val messages = new Messages("com.taskadapter.connector.trello.messages")

  override def isWebConnector = true

  override def formatError(e: Throwable): String = {
    if (e.isInstanceOf[ServerURLNotSetException]) return messages.get("errors.serverURLNotSet")
    if (e.isInstanceOf[LoginNameNotSpecifiedException]) return messages.get("errors.loginNameNotSet")
    if (!e.isInstanceOf[UnsupportedConnectorOperation]) return e.getMessage
    val connEx = e.asInstanceOf[UnsupportedConnectorOperation]
    if ("saveRelations" == connEx.getMessage) return messages.get("errors.unsupported.relations")
    e.getMessage
  }

  override def getMiniPanelContents(sandbox: Sandbox, config: TrelloConfig, setup: WebConnectorSetup): ComponentContainer = {
    val layout = new VerticalLayout
    layout.setWidth(380, PIXELS)
    type C = util.List[_ <: NamedKeyedObject]

    val projectPanel = new ProjectPanel(new ObjectProperty[String](config.boardId),
      new ObjectProperty[String](config.boardId),
      Interfaces.fromMethod(classOf[DataProvider[C]], classOf[TrelloConfig], "getProjects", setup), null, null, this)
    projectPanel.setProjectKeyLabel("Repository ID")
    layout.addComponent(projectPanel)
    layout
  }

  override def getEditSetupPanel(sandbox: Sandbox, setup: WebConnectorSetup): ConnectorSetupPanel =
    ServerPanelFactory.withApiKeyAndToken(TrelloConnector.ID, TrelloConnector.ID, setup)

  @throws[BadConfigException]
  override def validateForSave(config: TrelloConfig, serverInfo: WebConnectorSetup): Unit = {
    if (Strings.isNullOrEmpty(serverInfo.host)) throw new ServerURLNotSetException
    if (Strings.isNullOrEmpty(serverInfo.userName)) throw new LoginNameNotSpecifiedException
  }

  @throws[BadConfigException]
  override def validateForLoad(config: TrelloConfig, serverInfo: WebConnectorSetup): Unit =
    if (Strings.isNullOrEmpty(serverInfo.host)) throw new ServerURLNotSetException

  override def describeSourceLocation(config: TrelloConfig, setup: WebConnectorSetup): String = setup.host

  override def describeDestinationLocation(config: TrelloConfig, setup: WebConnectorSetup): String =
    describeSourceLocation(config, setup)

  @throws[BadConfigException]
  override def updateForSave(config: TrelloConfig, sandbox: Sandbox, setup: WebConnectorSetup): WebConnectorSetup = {
    validateForSave(config, setup)
    setup
  }

  @throws[BadConfigException]
  @throws[DroppingNotSupportedException]
  override def validateForDropInLoad(config: TrelloConfig) = throw DroppingNotSupportedException.INSTANCE

  override def createDefaultSetup(): WebConnectorSetup = WebConnectorSetup(TrelloConnector.ID, None, "My Trello",
    "https://api.trello.com", "", "", false, "")
}