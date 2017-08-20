package com.taskadapter.connector.mantis

import java.util

import com.google.gson.JsonElement
import com.taskadapter.connector.Field
import com.taskadapter.connector.common.ConfigUtils
import com.taskadapter.connector.definition.{Descriptor, PluginFactory, WebConnectorSetup}
import com.taskadapter.model.StandardField

import scala.collection.JavaConverters._
import scala.collection.immutable.Map

object MantisFactory {
  private val DESCRIPTOR = Descriptor(MantisConnector.ID, MantisConfig.DEFAULT_LABEL)
}

class MantisFactory extends PluginFactory[MantisConfig, WebConnectorSetup] {
  override def getAvailableFields: util.List[Field] = MantisField.fields.asJava

  override def getSuggestedCombinations: Map[Field, StandardField] = MantisField.getSuggestedCombinations()

  def createConnector(config: MantisConfig, setup: WebConnectorSetup) = new MantisConnector(config, setup)

  override def getDescriptor: Descriptor = MantisFactory.DESCRIPTOR

  override def writeConfig(config: MantisConfig): JsonElement = ConfigUtils.createDefaultGson.toJsonTree(config)

  override def readConfig(config: JsonElement): MantisConfig = ConfigUtils.createDefaultGson.fromJson(config, classOf[MantisConfig])

  override def createDefaultConfig = new MantisConfig
}
