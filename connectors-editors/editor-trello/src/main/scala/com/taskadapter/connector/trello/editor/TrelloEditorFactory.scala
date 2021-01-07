package com.taskadapter.connector.trello.editor

import com.google.common.base.Strings
import com.taskadapter.connector.definition.exception.FieldNotMappedException
import com.taskadapter.connector.definition.exceptions._
import com.taskadapter.connector.definition.{FieldMapping, WebConnectorSetup}
import com.taskadapter.connector.trello.{TrelloClient, TrelloConfig, TrelloConnector}
import com.taskadapter.model.{NamedKeyedObjectImpl, TaskStatus}
import com.taskadapter.vaadin14shim.VerticalLayout
import com.taskadapter.web.configeditor.server.{ProjectPanelScala, ServerPanelFactory}
import com.taskadapter.web.data.Messages
import com.taskadapter.web.service.Sandbox
import com.taskadapter.web.{ConnectorSetupPanel, DroppingNotSupportedException, PluginEditorFactory}
import com.vaadin.data.util.MethodProperty
import com.vaadin.server.Sizeable.Unit.PIXELS
import com.vaadin.ui.HasComponents

import scala.collection.mutable

class TrelloEditorFactory extends PluginEditorFactory[TrelloConfig, WebConnectorSetup] {
  private val messages = new Messages("com.taskadapter.connector.trello.messages")

  override def isWebConnector = true

  override def formatError(e: Throwable): String = {
    if (e.isInstanceOf[ServerURLNotSetException]) return messages.get("errors.serverURLNotSet")
    if (e.isInstanceOf[LoginNameNotSpecifiedException]) return messages.get("errors.loginNameNotSet")
    if (e.isInstanceOf[ProjectNotSetException]) return messages.get("errors.boardNotSet")
    if (e.isInstanceOf[FieldNotMappedException]) return messages.format("trello.error.requiredFieldNotMapped",
      e.asInstanceOf[FieldNotMappedException].fieldName)
    if (!e.isInstanceOf[UnsupportedConnectorOperation]) return e.getMessage
    val connEx = e.asInstanceOf[UnsupportedConnectorOperation]
    if ("saveRelations" == connEx.getMessage) return messages.get("errors.unsupported.relations")
    e.getMessage
  }

  override def getMiniPanelContents(sandbox: Sandbox, config: TrelloConfig, setup: WebConnectorSetup): HasComponents = {
    val layout = new VerticalLayout
    layout.setWidth(600, PIXELS)
    val client = new TrelloClient(setup.password, setup.apiKey)
    val projectPanel = new ProjectPanelScala(messages.get("projectPanel.projectLabel"),
      new MethodProperty[String](config, "boardName"),
      new MethodProperty[String](config, "boardId"),
      () => {
        client.getBoards(setup.userName).map(b => NamedKeyedObjectImpl(b.getId, b.getName))
      },
      this)
    layout.add(projectPanel)
    layout
  }

  override def getEditSetupPanel(sandbox: Sandbox, setup: WebConnectorSetup): ConnectorSetupPanel =
    ServerPanelFactory.withApiKeyAndToken(TrelloConnector.ID, TrelloConnector.ID, setup)

  override def validateForSave(config: TrelloConfig, serverInfo: WebConnectorSetup, fieldMappings: Seq[FieldMapping[_]]):
    Seq[BadConfigException] = {
    val seq = new mutable.ListBuffer[BadConfigException]
    if (Strings.isNullOrEmpty(serverInfo.host)) seq += new ServerURLNotSetException
    if (Strings.isNullOrEmpty(serverInfo.userName)) seq += new LoginNameNotSpecifiedException
    if (Strings.isNullOrEmpty(config.boardId)) seq += new ProjectNotSetException

    seq ++ checkTrelloListIsMapped(fieldMappings)
  }

  def checkTrelloListIsMapped(fieldMappings: Seq[FieldMapping[_]]): Seq[BadConfigException] = {
    // "List Name" must be present on the right side and must be selected for export
    if (!fieldMappings.exists(m => m.fieldInConnector2.contains(TaskStatus) && m.selected)) {
      return Seq(FieldNotMappedException("List Name"))
    }
    Seq()
  }

  override def validateForLoad(config: TrelloConfig, serverInfo: WebConnectorSetup): Seq[BadConfigException] = {
    val seq = new mutable.ListBuffer[BadConfigException]
    if (Strings.isNullOrEmpty(serverInfo.host)) seq += new ServerURLNotSetException
    if (Strings.isNullOrEmpty(config.boardId)) seq += new ProjectNotSetException
    seq
  }

  override def describeSourceLocation(config: TrelloConfig, setup: WebConnectorSetup): String = setup.host

  override def describeDestinationLocation(config: TrelloConfig, setup: WebConnectorSetup): String =
    describeSourceLocation(config, setup)

  @throws[DroppingNotSupportedException]
  override def validateForDropInLoad(config: TrelloConfig) = throw DroppingNotSupportedException.INSTANCE

  override def createDefaultSetup(sandbox: Sandbox): WebConnectorSetup = WebConnectorSetup(TrelloConnector.ID, None, "My Trello",
    "https://api.trello.com", "", "", false, "")

  override def fieldNames: Messages = new Messages("com.taskadapter.connector.trello.field-names")
}