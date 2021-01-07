package com.taskadapter.connector.mantis.editor

import java.util
import com.google.common.base.Strings
import com.taskadapter.vaadin14shim.VerticalLayout
import com.taskadapter.vaadin14shim.HorizontalLayout
import com.taskadapter.connector.definition.exceptions.{BadConfigException, ProjectNotSetException, ServerURLNotSetException}
import com.taskadapter.connector.definition.{FieldMapping, WebConnectorSetup}
import com.taskadapter.connector.mantis.{MantisConfig, MantisConnector}
import com.taskadapter.web.configeditor.ProjectPanel
import com.taskadapter.web.configeditor.server.ServerPanelFactory
import com.taskadapter.web.data.Messages
import com.taskadapter.web.service.Sandbox
import com.taskadapter.web.{ConnectorSetupPanel, DroppingNotSupportedException, PluginEditorFactory}
import com.vaadin.data.util.MethodProperty
import com.vaadin.server.Sizeable.Unit.PIXELS
import com.vaadin.ui.HasComponents

import scala.collection.{JavaConverters, Seq, mutable}

class MantisEditorFactory extends PluginEditorFactory[MantisConfig, WebConnectorSetup] {
  private val BUNDLE_NAME = "com.taskadapter.connector.mantis.editor.messages"
  private val MESSAGES = new Messages(BUNDLE_NAME)

  override def isWebConnector = true

  override def getEditSetupPanel(sandbox: Sandbox, setup: WebConnectorSetup): ConnectorSetupPanel =
    ServerPanelFactory.withLoginAndPassword(MantisConnector.ID, MantisConnector.ID, setup)

  override def createDefaultSetup(sandbox: Sandbox) = new WebConnectorSetup(MantisConnector.ID, Option.empty,
    "My MantisBT", "http://", "", "", false, "")

  override def formatError(e: Throwable): String = {
    if (e.isInstanceOf[ProjectNotSetException]) return MESSAGES.get("error.projectNotSet")
    if (e.isInstanceOf[ServerURLNotSetException]) return MESSAGES.get("error.serverUrlNotSet")
    if (e.isInstanceOf[BothProjectKeyAndQueryIsAreMissingException]) return MESSAGES.get("mantisbt.error.bothProjectKeyAndQueryIdAreMissing")
    if (e.isInstanceOf[UnsupportedOperationException]) {
      val uop = e.asInstanceOf[UnsupportedOperationException]
      if ("saveRelations" == uop.getMessage) return MESSAGES.get("error.unsupported.relations")
    }
    e.toString
  }

  override def getMiniPanelContents(sandbox: Sandbox, config: MantisConfig, setup: WebConnectorSetup): HasComponents = {
    val layout = new VerticalLayout
    layout.setWidth(380, PIXELS)
    layout.add(new ProjectPanel(new MethodProperty[String](config, "projectKey"),
      Option.apply(new MethodProperty[java.lang.Long](config, "queryId")),
      Option.empty, new MantisProjectsListLoader(setup), null, new MantisQueryListLoader(config, setup), this))
    layout.add(new OtherMantisFieldsPanel(config))
    layout
  }

  override def validateForSave(config: MantisConfig, setup: WebConnectorSetup, fieldMappings: Seq[FieldMapping[_]]): Seq[BadConfigException] = {
    val list = new util.ArrayList[BadConfigException]
    if (Strings.isNullOrEmpty(setup.host)) list.add(new ServerURLNotSetException)
    if (config.getProjectKey == null || config.getProjectKey.isEmpty) list.add(new ProjectNotSetException)
    JavaConverters.asScalaBuffer(list)
  }

  override def validateForLoad(config: MantisConfig, setup: WebConnectorSetup): Seq[BadConfigException] = {
    val seq = new mutable.ListBuffer[BadConfigException]()
    if (Strings.isNullOrEmpty(setup.host)) seq += new ServerURLNotSetException
    if (Strings.isNullOrEmpty(config.getProjectKey) && config.getQueryId == null) seq += new BothProjectKeyAndQueryIsAreMissingException
    seq
  }

  override def describeSourceLocation(config: MantisConfig, setup: WebConnectorSetup): String = setup.host

  override def describeDestinationLocation(config: MantisConfig, setup: WebConnectorSetup): String = describeSourceLocation(config, setup)

  override def fieldNames: Messages = MESSAGES

  @throws[DroppingNotSupportedException]
  override def validateForDropInLoad(config: MantisConfig) = throw DroppingNotSupportedException.INSTANCE
}