package com.taskadapter.web.uiapi

import com.taskadapter.connector.NewConnector
import com.taskadapter.connector.definition.exceptions.BadConfigException
import com.taskadapter.connector.definition.{ConnectorSetup, FieldMapping}
import com.taskadapter.model.Field
import com.taskadapter.web.DroppingNotSupportedException
import com.taskadapter.web.data.Messages
import com.taskadapter.web.service.Sandbox

import scala.collection.Seq


/**
  * Rich connector configuration item. Provides useful utility methods to create
  * UI elements, deserialize configs, etc... Simplifies PluginEditorFactory access
  * by tightly binding it to config (no more "resolve" operations). Also hides
  * "connector configuration type" inside implementation and removes need to
  * specify source/target types via all pages/controllers/etc... (Yes, it is a
  * java-specific way to implement "existential types").
  *
  */
abstract class UIConnectorConfig {
  /**
    * Returns a "connector type" id.
    *
    * @return connector type id.
    */
  def getConnectorTypeId: String

  /**
    * Returns a connector configuration in a "string" format. This
    * configuration along with {@link #getConnectorTypeId()} may be passed into
    * {@link UIConfigService#createRichConfig(String, String)} to create a
    * clone of this config.
    *
    * @return string representation of UI connector config.
    */
  def getConfigString: String

  /**
    * Calculates and returns connector label. This label may change when
    * connector changes a configuration.
    *
    * @return connector user-friendly label.
    */
  def getLabel: String

  def getConnectorSetup: ConnectorSetup

  def setConnectorSetup(setup: ConnectorSetup): Unit

  /**
    * Validates config for load.
    * @return list of config errors. empty is no errors found. never null
    */
  def validateForLoad(): Seq[BadConfigException]

  /**
    * Validates config for save. Does not update it in any way.
    */
  def validateForSave(fieldMappings: java.util.List[FieldMapping[_]]): Seq[BadConfigException]

  /**
    * Validates config for drop-in operation.
    */
  @throws[BadConfigException]
  @throws[DroppingNotSupportedException]
  def validateForDropIn(): Unit

  /**
    * Creates a new connector instance with a current config. Note, that
    * current config will be shared among all created connectors.
    *
    * @return new connector instance, which shares config with this UIConfig
    *         and all other connectors created via this method.
    */
  def createConnectorInstance: NewConnector

  /**
    * Creates a "mini" informational panel.
    *
    * @param sandbox user operations sandbox.
    * @return connector configuration panel.
    */
  def createMiniPanel(sandbox: Sandbox): SavableComponent

  def getAllFields: Seq[Field[_]]

  def getDefaultFieldsForNewConfig: Seq[Field[_]]

  /**
    * Returns a source location name. This name is just a string for a user.
    *
    * @return source location name.
    */
  def getSourceLocation: String

  /**
    * Returns a destination location name. This name is just a string for a user.
    *
    * @return destination location name.
    */
  def getDestinationLocation: String

  def fieldNames: Messages

  /**
    * Decodes a connector exception into a user-friendly (possibly localized) message.
    *
    * @param e exception to decode.
    * @return user-friendly error description.
    */
  def decodeException(e: Throwable): String
}
