package com.taskadapter.connector.basecamp

import java.util

import com.google.gson.{JsonElement, JsonObject, JsonParseException}
import com.taskadapter.connector.Field
import com.taskadapter.connector.basecamp.transport.{BaseCommunicator, ObjectAPIFactory}
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

  override def writeConfig(config: BasecampConfig): JsonElement = {
    val res = new JsonObject
    res.addProperty("version", 1)
    setp(res, "label", config.getLabel)
    setp(res, "account", config.getAccountId)
    setp(res, "projectKey", config.getProjectKey)
    setp(res, "todoKey", config.getTodoKey)
    setp(res, "loadCompletedTodos", java.lang.Boolean.toString(config.getLoadCompletedTodos))
    setp(res, "findUserByName", java.lang.Boolean.toString(config.isFindUserByName))
    res
  }

  @throws[JsonParseException]
  override def readConfig(config: JsonElement): BasecampConfig = {
    val obj = config.getAsJsonObject
    val res = new BasecampConfig
    res.setLabel(getS("label", obj))
    res.setAccountId(getS("account", obj))
    res.setProjectKey(getS("projectKey", obj))
    res.setTodoKey(getS("todoKey", obj))
    res.setLoadCompletedTodos(java.lang.Boolean.parseBoolean(getS("loadCompletedTodos", obj)))
    res.setFindUserByName(java.lang.Boolean.parseBoolean(getS("findUserByName", obj)))
    res
  }

  override def createDefaultConfig = new BasecampConfig

  private def getO(property: String, config: JsonObject): JsonObject = {
    val elt = config.get(property)
    if (elt == null || !elt.isJsonObject) return null
    elt.getAsJsonObject
  }

  private def getS(property: String, config: JsonObject): String = {
    val elt = config.get(property)
    if (elt == null || !elt.isJsonPrimitive) return null
    elt.getAsString
  }


  def setp(obj: JsonObject, name: String, value: JsonElement): Unit = {
    if (value == null) return
    obj.add(name, value)
  }

  def setp(obj: JsonObject, name: String, value: String): Unit = {
    if (value == null) return
    obj.addProperty(name, value)
  }
}