package com.taskadapter.connector.redmine

import java.util

import com.google.gson.{JsonElement, JsonParseException}
import com.taskadapter.connector.Field
import com.taskadapter.connector.common.ConfigUtils
import com.taskadapter.connector.definition.{Descriptor, PluginFactory, WebServerInfo}
import com.taskadapter.model.StandardField

import scala.collection.immutable.Map

class RedmineFactory extends PluginFactory[RedmineConfig] {
  private val DESCRIPTOR = Descriptor(RedmineConnector.ID, "Redmine")

  override def getAvailableFields: util.List[Field] = RedmineField.fieldsAsJava()

  override def getSuggestedCombinations: Map[Field, StandardField] = RedmineField.getSuggestedCombinations()

  override def createConnector(config: RedmineConfig, serverInfo: WebServerInfo) = new RedmineConnector(config, serverInfo)

  override def getDescriptor = DESCRIPTOR

  override def writeConfig(config: RedmineConfig): JsonElement = ConfigUtils.createDefaultGson.toJsonTree(config)

  @throws[JsonParseException]
  override def readConfig(config: JsonElement): RedmineConfig = ConfigUtils.createDefaultGson.fromJson(config, classOf[RedmineConfig])

  override def createDefaultConfig = new RedmineConfig
}
