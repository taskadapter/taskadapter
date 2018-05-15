package com.taskadapter.connector.basecamp

import com.google.gson.{JsonElement, JsonParseException}
import com.taskadapter.connector.basecamp.transport.{BaseCommunicator, ObjectAPIFactory}
import com.taskadapter.connector.common.ConfigUtils
import com.taskadapter.connector.definition.{Descriptor, PluginFactory, WebConnectorSetup}
import com.taskadapter.model.Field

class BasecampFactory extends PluginFactory[BasecampConfig, WebConnectorSetup] {
  val DESCRIPTOR = Descriptor(BasecampConnector.ID, "Basecamp 2")
  final private val factory = new ObjectAPIFactory(new BaseCommunicator)

  override def createConnector(config: BasecampConfig, setup: WebConnectorSetup) = new BasecampConnector(config, setup, factory)

  override def getSuggestedCombinations: Seq[Field[_]] = BasecampField.fields

  override def getDescriptor = DESCRIPTOR

  override def writeConfig(config: BasecampConfig): JsonElement = ConfigUtils.createDefaultGson.toJsonTree(config)

  @throws[JsonParseException]
  override def readConfig(config: JsonElement): BasecampConfig =
    ConfigUtils.createDefaultGson.fromJson(config, classOf[BasecampConfig])

  override def createDefaultConfig = new BasecampConfig
}