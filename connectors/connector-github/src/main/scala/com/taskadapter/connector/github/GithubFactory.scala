package com.taskadapter.connector.github
import java.util

import com.google.gson.{JsonElement, JsonParseException}
import com.taskadapter.connector.Field
import com.taskadapter.connector.common.ConfigUtils
import com.taskadapter.connector.definition.{Descriptor, PluginFactory, WebConnectorSetup}
import com.taskadapter.model.StandardField

import scala.collection.immutable.Map


object GithubFactory {
  val DESCRIPTOR = Descriptor(GithubConnector.ID, GithubConfig.DEFAULT_LABEL)
}

class GithubFactory extends PluginFactory[GithubConfig, WebConnectorSetup] {
  override def getAvailableFields: util.List[Field] = GithubField.fieldsAsJava()


  override def getSuggestedCombinations: Map[Field, StandardField] = GithubField.getSuggestedCombinations()

  def createConnector(config: GithubConfig, setup: WebConnectorSetup) = new GithubConnector(config, setup)

  override def getDescriptor: Descriptor = GithubFactory.DESCRIPTOR

  override def writeConfig(config: GithubConfig): JsonElement = ConfigUtils.createDefaultGson.toJsonTree(config)

  @throws[JsonParseException]
  override def readConfig(config: JsonElement): GithubConfig = ConfigUtils.createDefaultGson.fromJson(config, classOf[GithubConfig])

  override def createDefaultConfig = new GithubConfig
}
