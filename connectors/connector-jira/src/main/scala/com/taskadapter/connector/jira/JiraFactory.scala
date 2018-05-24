package com.taskadapter.connector.jira

import com.google.gson.{JsonElement, JsonParseException}
import com.taskadapter.connector.common.ConfigUtils
import com.taskadapter.connector.definition.{Descriptor, PluginFactory, WebConnectorSetup}
import com.taskadapter.model.Field

class JiraFactory extends PluginFactory[JiraConfig, WebConnectorSetup] {
  private val DESCRIPTOR = Descriptor(JiraConnector.ID, "Atlassian JIRA")

  override def getAllFields: Seq[Field[_]] = JiraField.fields

  override def createConnector(config: JiraConfig, setup: WebConnectorSetup) = new JiraConnector(config, setup)

  override def getDescriptor = DESCRIPTOR

  override def writeConfig(config: JiraConfig): JsonElement = ConfigUtils.createDefaultGson.toJsonTree(config)

  @throws[JsonParseException]
  override def readConfig(config: JsonElement): JiraConfig = ConfigUtils.createDefaultGson.fromJson(config, classOf[JiraConfig])

  override def createDefaultConfig = new JiraConfig

  override def getDefaultFieldsForNewConfig: Seq[Field[_]] = JiraField.defaultFieldsForNewConfig
}
