package com.taskadapter.connector.github.editor

import com.google.common.base.Strings
import com.taskadapter.connector.ValidationErrorBuilder
import com.taskadapter.connector.definition.{FieldMapping, WebConnectorSetup}
import com.taskadapter.connector.definition.exception.ConfigValidationError
import com.taskadapter.connector.definition.exceptions.{BadConfigException, LoginNameNotSpecifiedException, ProjectNotSetException, ServerURLNotSetException, UnsupportedConnectorOperation}
import com.taskadapter.connector.github.{GithubConfig, GithubConnector}
import com.taskadapter.web.{ConnectorSetupPanel, DroppingNotSupportedException, PluginEditorFactory}
import com.taskadapter.web.configeditor.ProjectPanel
import com.taskadapter.web.configeditor.server.ServerPanelFactory
import com.taskadapter.web.data.Messages
import com.taskadapter.web.service.Sandbox
import com.vaadin.data.util.MethodProperty
import com.vaadin.server.Sizeable.Unit.PIXELS
import com.vaadin.ui.{ComponentContainer, VerticalLayout}

import scala.collection.Seq

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

  override def getMiniPanelContents(sandbox: Sandbox, config: GithubConfig, setup: WebConnectorSetup): ComponentContainer = {
    val layout = new VerticalLayout
    layout.setWidth(380, PIXELS)
    val projectPanel = new ProjectPanel(new MethodProperty[String](config, "projectKey"), Option.empty,
      Option.apply(new MethodProperty[String](config, "queryString")),
      new GithubProjectsListLoader(setup), null, null, this)
    projectPanel.setProjectKeyLabel("Repository ID")
    layout.addComponent(projectPanel)
    layout
  }

  override def getEditSetupPanel(sandbox: Sandbox, setup: WebConnectorSetup): ConnectorSetupPanel =
    ServerPanelFactory.withLoginAndPassword(GithubConnector.ID, GithubConnector.ID, setup)

  override def createDefaultSetup = new WebConnectorSetup(
    GithubConnector.ID, Option.empty, "My GitHub", "https://github.com", "", "", false, "")

  @throws[BadConfigException]
  override def validateForSave(config: GithubConfig, serverInfo: WebConnectorSetup, fieldMappings: Seq[FieldMapping[_]]): Unit = {
    if (Strings.isNullOrEmpty(serverInfo.host)) throw new ServerURLNotSetException
    if (Strings.isNullOrEmpty(serverInfo.userName)) throw new LoginNameNotSpecifiedException
  }

  override def validateForLoad(config: GithubConfig, serverInfo: WebConnectorSetup): Seq[ConfigValidationError] = {
    val builder = new ValidationErrorBuilder
    if (Strings.isNullOrEmpty(serverInfo.host)) builder.error(new ServerURLNotSetException)
    if (Strings.isNullOrEmpty(config.getProjectKey)) builder.error(new ProjectNotSetException)
    builder.build
  }

  override def describeSourceLocation(config: GithubConfig, setup: WebConnectorSetup): String = setup.host

  override def describeDestinationLocation(config: GithubConfig, setup: WebConnectorSetup): String = describeSourceLocation(config, setup)

  override def fieldNames: Messages = MESSAGES

  @throws[BadConfigException]
  override def updateForSave(config: GithubConfig, sandbox: Sandbox, setup: WebConnectorSetup, fieldMappings: Seq[FieldMapping[_]]): WebConnectorSetup = {
    validateForSave(config, setup, fieldMappings)
    setup
  }

  @throws[DroppingNotSupportedException]
  override def validateForDropInLoad(config: GithubConfig) = throw DroppingNotSupportedException.INSTANCE
}