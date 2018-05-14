package com.taskadapter.connector.definition

import com.google.gson.{JsonElement, JsonParseException}
import com.taskadapter.connector.NewConnector
import com.taskadapter.model.Field

/**
  * TODO: Maybe get rid of this class? Configure binding between descriptor
  * and service in a config file? Or, at least, remove descriptor from this
  * plugin factory and leave this as a "connector factory" item.
  */
trait PluginFactory[C <: ConnectorConfig, S <: ConnectorSetup] {

  def getSuggestedCombinations: Seq[Field[_]]

  def createConnector(config: C, setup: S): NewConnector

  def getDescriptor: Descriptor

  /**
    * Serializes a config to a Json Element.
    *
    * @param config config to serialize.
    * @return serialized config.
    */
  def writeConfig(config: C): JsonElement

  /**
    * Reads a config.
    *
    * @param config config to read.
    * @return config from input.
    * @throws JsonParseException if config is in invalid format.
    */
  @throws[JsonParseException]
  def readConfig(config: JsonElement): C

  /**
    * Creates a default ("almost empty") connector config.
    *
    * @return new connector config.
    */
  def createDefaultConfig: C
}
