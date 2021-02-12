package com.taskadapter.connector.github.editor

import com.google.common.base.Strings
import com.taskadapter.connector.definition.exceptions.{BadConfigException, LoginNameNotSpecifiedException, ProjectNotSetException, ServerURLNotSetException, UnsupportedConnectorOperation}
import com.taskadapter.connector.definition.{FieldMapping, WebConnectorSetup}
import com.taskadapter.connector.github.{GithubConfig, GithubConnector}
import com.taskadapter.web.configeditor.ProjectPanel
import com.taskadapter.web.configeditor.server.ServerPanelWithLoginAndToken
import com.taskadapter.web.data.Messages
import com.taskadapter.web.service.Sandbox
import com.taskadapter.web.uiapi.{DefaultSavableComponent, SavableComponent}
import com.taskadapter.web.{ConnectorSetupPanel, DroppingNotSupportedException, PluginEditorFactory}
import com.vaadin.flow.data.binder.{Binder, ValidationException}

import scala.collection.JavaConverters._
import scala.collection.mutable

class GithubEditorFactory extends PluginEditorFactory[GithubConfig, WebConnectorSetup] {
  private val BUNDLE_NAME = "com.taskadapter.connector.github.messages"
  private val MESSAGES = new Messages(BUNDLE_NAME)

  override def isWebConnector = true

  override def formatError(e: Throwable): String = {
    if (e.isInstanceOf[ServerURLNotSetException]) return MESSAGES.get("errors.serverURLNotSet")
    if (e.isInstanceOf[ProjectNotSetException]) return MESSAGES.get("github.errors.projectNotSet")
    if (e.isInstanceOf[LoginNameNotSpecifiedException]) return MESSAGES.get("errors.loginNameNotSet")
    if (!e.isInstanceOf[UnsupportedConnectorOperation]) return e.getMessage
    val connEx = e.asInstanceOf[UnsupportedConnectorOperation]
    if ("saveRelations" == connEx.getMessage) return MESSAGES.get("errors.unsupported.relations")
    e.getMessage
  }

  override def getMiniPanelContents(sandbox: Sandbox, config: GithubConfig, setup: WebConnectorSetup): SavableComponent = {
    val binder = new Binder[GithubConfig](classOf[GithubConfig])

    val projectPanel = new ProjectPanel(binder,
      "projectKey",
      Option.empty,
      Option.apply("queryString"),
      new GithubProjectsListLoader(setup),
      null, null, this)
    projectPanel.setProjectKeyLabel("Repository ID")

    binder.readBean(config)

    new DefaultSavableComponent(projectPanel, () => {
      try {
        binder.writeBean(config)
        true
      } catch {
        case e: ValidationException =>
          e.printStackTrace()
          false
      }
    })
  }

  override def getEditSetupPanel(sandbox: Sandbox, setup: WebConnectorSetup): ConnectorSetupPanel = {
    val description = "Please generate an API token here: <br/>" +
      "<b>https://github.com/settings/tokens</b>"
    new ServerPanelWithLoginAndToken(GithubConnector.ID, GithubConnector.ID, setup, description)
  }

  override def createDefaultSetup(sandbox: Sandbox) = new WebConnectorSetup(
    GithubConnector.ID, Option.empty, "My GitHub", "https://github.com", "", "", false, "")

  @throws[BadConfigException]
  override def validateForSave(config: GithubConfig, serverInfo: WebConnectorSetup,
                               fieldMappings: java.util.List[FieldMapping[_]]): java.util.List[BadConfigException] = {
    val seq = new mutable.ListBuffer[BadConfigException]
    if (Strings.isNullOrEmpty(serverInfo.host)) seq += new ServerURLNotSetException
    if (Strings.isNullOrEmpty(serverInfo.userName)) seq += new LoginNameNotSpecifiedException
    seq.asJava
  }

  override def validateForLoad(config: GithubConfig, serverInfo: WebConnectorSetup): java.util.List[BadConfigException] = {
    val seq = new mutable.ListBuffer[BadConfigException]
    if (Strings.isNullOrEmpty(serverInfo.host)) seq += new ServerURLNotSetException
    if (Strings.isNullOrEmpty(config.getProjectKey)) seq += new ProjectNotSetException
    seq.asJava
  }

  override def describeSourceLocation(config: GithubConfig, setup: WebConnectorSetup): String = setup.host

  override def describeDestinationLocation(config: GithubConfig, setup: WebConnectorSetup): String = describeSourceLocation(config, setup)

  override def fieldNames: Messages = MESSAGES

  @throws[DroppingNotSupportedException]
  override def validateForDropInLoad(config: GithubConfig) = throw DroppingNotSupportedException.INSTANCE
}