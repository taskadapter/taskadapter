package com.taskadapter.web

import com.taskadapter.connector.definition.exceptions.BadConfigException
import com.taskadapter.connector.definition.{ConnectorConfig, ConnectorSetup, FieldMapping}
import com.taskadapter.web.data.Messages
import com.taskadapter.web.service.Sandbox
import com.vaadin.ui.HasComponents

import scala.collection.Seq

trait PluginEditorFactory[C <: ConnectorConfig, S <: ConnectorSetup] extends ExceptionFormatter {
  def getMiniPanelContents(sandbox: Sandbox, config: C, setup: S): HasComponents

  def isWebConnector: Boolean

  def getEditSetupPanel(sandbox: Sandbox, setup: S): ConnectorSetupPanel

  /**
    * @param sandbox this may used by file-based connectors, e.g. to generate a new file name inside user data folder
    */
  def createDefaultSetup(sandbox: Sandbox): S

  /**
    * Validates a connector config for save mode. If validation fails, plugin
    * editor factory should provide appropriate user-friendly message.
    *
    * @param config config to check.
    */
  def validateForSave(config: C, setup: S, fieldMappings: Seq[FieldMapping[_]]): Seq[BadConfigException]

  /**
    * Validates a connector config for load mode. If validation fails, plugin
    * editor factory should provide appropriate user-friendly message.
    *
    * @param config config to check.
    */
  def validateForLoad(config: C, setup: S): Seq[BadConfigException]

  /**
    * Validates config for "drop-in" loading.
    *
    * @param config config to validate.
    * @throws BadConfigException            if config cannot accept a drop for some reason.
    * @throws DroppingNotSupportedException if dropping is not supported either by this plugin or by
    *                                       "config type" (i.e. it's not a "configuration
    *                                       mistake", it is a definitely an "unsupported configuration").
    */
  @throws[BadConfigException]
  @throws[DroppingNotSupportedException]
  def validateForDropInLoad(config: C): Unit

  /**
    * Describes source location in a user-friendly manner.
    *
    * @return user-friendly description of a source location.
    */
  def describeSourceLocation(config: C, setup: S): String

  /**
    * Describes destination location in a user-friendly manner.
    *
    * @return user-friendly description of destination location.
    */
  def describeDestinationLocation(config: C, setup: S): String

  /**
    * Describes what labels to show for fields. e.g. "Status" field can be shown as "List Name" for Trello.
    */
  def fieldNames: Messages
}
