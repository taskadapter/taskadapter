package com.taskadapter.connector.basecamp

import java.util

import com.google.gson.{JsonElement, JsonParseException}
import com.taskadapter.connector.Field
import com.taskadapter.connector.basecamp.transport.{BaseCommunicator, ObjectAPIFactory}
import com.taskadapter.connector.common.ConfigUtils
import com.taskadapter.connector.definition.{Descriptor, PluginFactory, WebConnectorSetup}
import com.taskadapter.model.StandardField

import scala.collection.JavaConverters._
import scala.collection.immutable.Map

class BasecampFactory extends PluginFactory[BasecampConfig, WebConnectorSetup] {
  val DESCRIPTOR = Descriptor(BasecampConnector.ID, "Basecamp 2")
  final private val factory = new ObjectAPIFactory(new BaseCommunicator)

  override def createConnector(config: BasecampConfig, setup: WebConnectorSetup) = new BasecampConnector(config, setup, factory)

  override def getAvailableFields: util.List[Field] = BasecampField.fields.asJava

  override def getSuggestedCombinations: Map[Field, StandardField] = BasecampField.suggestedStandardFields

  override def getDescriptor = DESCRIPTOR

  override def writeConfig(config: BasecampConfig): JsonElement = ConfigUtils.createDefaultGson.toJsonTree(config)

  @throws[JsonParseException]
  override def readConfig(config: JsonElement): BasecampConfig =
    ConfigUtils.createDefaultGson.fromJson(config, classOf[BasecampConfig])

  override def createDefaultConfig = new BasecampConfig
}