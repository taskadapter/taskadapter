package com.taskadapter.connector.trello

import com.google.gson.{JsonElement, JsonParseException}
import com.taskadapter.connector.common.ConfigUtils
import com.taskadapter.connector.definition.{Descriptor, PluginFactory, WebConnectorSetup}
import com.taskadapter.model.Field

class TrelloFactory extends PluginFactory[TrelloConfig, WebConnectorSetup] {

  override def getDescriptor = new Descriptor(TrelloConnector.ID, "Trello")

  override def getAllFields: java.util.List[Field[_]] = TrelloField.fields

  override def createConnector(config: TrelloConfig, setup: WebConnectorSetup) = new TrelloConnector(config, setup)

  override def writeConfig(config: TrelloConfig): JsonElement = ConfigUtils.createDefaultGson.toJsonTree(config)

  @throws[JsonParseException]
  override def readConfig(config: JsonElement): TrelloConfig = ConfigUtils.createDefaultGson.fromJson(config, classOf[TrelloConfig])

  override def createDefaultConfig = new TrelloConfig()

  override def getDefaultFieldsForNewConfig: java.util.List[Field[_]] = TrelloField.defaultFieldsForNewConfig
}
