package com.taskadapter.web.uiapi

import java.util

import com.taskadapter.connector.definition.ConnectorSetup
import com.taskadapter.connector.{Field, NewConnector}
import com.taskadapter.connector.definition.exceptions.BadConfigException
import com.taskadapter.model.StandardField
import com.taskadapter.web.DroppingNotSupportedException
import com.taskadapter.web.service.Sandbox
import com.vaadin.ui.ComponentContainer

import scala.beans.BeanProperty
import scala.collection.immutable.Map


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
    *
    * @throws BadConfigException
    * if config is invalid.
    */
  @throws[BadConfigException]
  def validateForLoad(): Unit

  /**
    * Validates config for save. Does not update it in any way.
    */
  @throws[BadConfigException]
  def validateForSave(): Unit

  /**
    * Validates config for drop-in operation.
    */
  @throws[BadConfigException]
  @throws[DroppingNotSupportedException]
  def validateForDropIn(): Unit

  /**
    * Validates and updates config for save. This method is used mostly by the
    * file-based connectors. Such connector may create a new file for the
    * export. However, web-based connectors also may perform some action in this method.
    *
    * @param sandbox local filesystem sandbox.
    * @return true iff config was updated.
    * @throws BadConfigException if config is invalid and cannot be fixed automatically.
    */
  @throws[BadConfigException]
  def updateForSave(sandbox: Sandbox): Boolean

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
  def createMiniPanel(sandbox: Sandbox): ComponentContainer

  /**
    * @return list of connector available fields (in current configuration).
    */
  def getAvailableFields: util.List[Field]

  def getSuggestedCombinations: Map[Field, StandardField]

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

  /**
    * Decodes a connector exception into a user-friendly (possibly localized) message.
    *
    * @param e exception to decode.
    * @return user-friendly error description.
    */
  def decodeException(e: Throwable): String
}
