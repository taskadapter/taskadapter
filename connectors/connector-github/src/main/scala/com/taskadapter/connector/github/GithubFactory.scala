package com.taskadapter.connector.github

import com.google.gson.{JsonElement, JsonParseException}
import com.taskadapter.connector.common.ConfigUtils
import com.taskadapter.connector.definition.{Descriptor, PluginFactory, WebConnectorSetup}
import com.taskadapter.model.Field

object GithubFactory {
  val DESCRIPTOR = Descriptor(GithubConnector.ID, GithubConfig.DEFAULT_LABEL)
}

class GithubFactory extends PluginFactory[GithubConfig, WebConnectorSetup] {
  override def getAllFields: Seq[Field[_]] = GithubField.fields

  def createConnector(config: GithubConfig, setup: WebConnectorSetup) = new GithubConnector(config, setup)

  override def getDescriptor: Descriptor = GithubFactory.DESCRIPTOR

  override def writeConfig(config: GithubConfig): JsonElement = ConfigUtils.createDefaultGson.toJsonTree(config)

  @throws[JsonParseException]
  override def readConfig(config: JsonElement): GithubConfig = ConfigUtils.createDefaultGson.fromJson(config, classOf[GithubConfig])

  override def createDefaultConfig = new GithubConfig

  override def getDefaultFieldsForNewConfig: Seq[Field[_]] = GithubField.fields
}
