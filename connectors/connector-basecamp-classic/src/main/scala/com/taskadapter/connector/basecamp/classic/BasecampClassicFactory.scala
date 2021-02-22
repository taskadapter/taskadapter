package com.taskadapter.connector.basecamp.classic

import com.google.gson.{JsonElement, JsonParseException}
import com.taskadapter.connector.basecamp.classic.transport.{BaseCommunicator, ObjectAPIFactory}
import com.taskadapter.connector.common.ConfigUtils
import com.taskadapter.connector.definition.{Descriptor, PluginFactory, WebConnectorSetup}
import com.taskadapter.model.Field

class BasecampClassicFactory extends PluginFactory[BasecampClassicConfig, WebConnectorSetup] {
  val DESCRIPTOR = Descriptor(BasecampClassicConnector.ID, "Basecamp Classic")
  final private val factory = new ObjectAPIFactory(new BaseCommunicator)

  override def createConnector(config: BasecampClassicConfig, setup: WebConnectorSetup) = new BasecampClassicConnector(config, setup, factory)

  override def getAllFields: java.util.List[Field[_]] = BasecampClassicField.fields

  override def getDescriptor = DESCRIPTOR

  override def writeConfig(config: BasecampClassicConfig): JsonElement = ConfigUtils.createDefaultGson.toJsonTree(config)

  @throws[JsonParseException]
  override def readConfig(config: JsonElement): BasecampClassicConfig =
    ConfigUtils.createDefaultGson.fromJson(config, classOf[BasecampClassicConfig])

  override def createDefaultConfig = new BasecampClassicConfig

  override def getDefaultFieldsForNewConfig: java.util.List[Field[_]] = BasecampClassicField.fields
}
