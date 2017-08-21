package com.taskadapter.connector.trello

import java.util

import com.google.gson.{JsonElement, JsonParseException}
import com.taskadapter.connector.Field
import com.taskadapter.connector.common.ConfigUtils
import com.taskadapter.connector.definition.{Descriptor, PluginFactory, WebConnectorSetup}
import com.taskadapter.model.StandardField

import scala.collection.JavaConverters._
import scala.collection.immutable.Map

class TrelloFactory extends PluginFactory[TrelloConfig, WebConnectorSetup] {

  override def getDescriptor = Descriptor(TrelloConnector.ID, "Trello")

  override def getAvailableFields: util.List[Field] = TrelloField.fields.asJava

  override def getSuggestedCombinations: Map[Field, StandardField] = TrelloField.getSuggestedCombinations()

  override def createConnector(config: TrelloConfig, setup: WebConnectorSetup) = new TrelloConnector(config, setup)

  override def writeConfig(config: TrelloConfig): JsonElement = ConfigUtils.createDefaultGson.toJsonTree(config)

  @throws[JsonParseException]
  override def readConfig(config: JsonElement): TrelloConfig = ConfigUtils.createDefaultGson.fromJson(config, classOf[TrelloConfig])

  override def createDefaultConfig = TrelloConfig("")
}
