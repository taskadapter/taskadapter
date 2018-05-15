package com.taskadapter.connector.mantis

import com.google.gson.JsonElement
import com.taskadapter.connector.common.ConfigUtils
import com.taskadapter.connector.definition.{Descriptor, PluginFactory, WebConnectorSetup}
import com.taskadapter.model.Field

object MantisFactory {
  private val DESCRIPTOR = Descriptor(MantisConnector.ID, MantisConfig.DEFAULT_LABEL)
}

class MantisFactory extends PluginFactory[MantisConfig, WebConnectorSetup] {

  override def getSuggestedCombinations: Seq[Field[_]] = MantisField.fields

  def createConnector(config: MantisConfig, setup: WebConnectorSetup) = new MantisConnector(config, setup)

  override def getDescriptor: Descriptor = MantisFactory.DESCRIPTOR

  override def writeConfig(config: MantisConfig): JsonElement = ConfigUtils.createDefaultGson.toJsonTree(config)

  override def readConfig(config: JsonElement): MantisConfig = ConfigUtils.createDefaultGson.fromJson(config, classOf[MantisConfig])

  override def createDefaultConfig = new MantisConfig
}
