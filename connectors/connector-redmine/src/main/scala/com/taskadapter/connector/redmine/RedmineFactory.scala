package com.taskadapter.connector.redmine

import com.google.gson.{JsonElement, JsonParseException}
import com.taskadapter.connector.common.ConfigUtils
import com.taskadapter.connector.definition.{Descriptor, PluginFactory, WebConnectorSetup}
import com.taskadapter.model.Field

class RedmineFactory extends PluginFactory[RedmineConfig, WebConnectorSetup] {
  private val DESCRIPTOR = Descriptor(RedmineConnector.ID, "Redmine")

  override def getAllFields: Seq[Field[_]] = RedmineField.fields

  override def createConnector(config: RedmineConfig, setup: WebConnectorSetup) = new RedmineConnector(config, setup)

  override def getDescriptor = DESCRIPTOR

  override def writeConfig(config: RedmineConfig): JsonElement = ConfigUtils.createDefaultGson.toJsonTree(config)

  @throws[JsonParseException]
  override def readConfig(config: JsonElement): RedmineConfig = ConfigUtils.createDefaultGson.fromJson(config, classOf[RedmineConfig])

  override def createDefaultConfig = new RedmineConfig

  override def getDefaultFieldsForNewConfig: Seq[Field[_]] = RedmineField.defaultFieldsForNewConfig
}
