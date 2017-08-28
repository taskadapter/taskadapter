package com.taskadapter.web.uiapi

import java.io.File
import java.util
import java.util.Date

import com.taskadapter.connector.MappingBuilder
import com.taskadapter.connector.common.ProgressMonitorUtils
import com.taskadapter.connector.definition._
import com.taskadapter.connector.definition.exceptions.ConnectorException
import com.taskadapter.core._
import com.taskadapter.model.GTask
import com.taskadapter.webui.results.ExportResultFormat

import scala.beans.BeanProperty
import scala.collection.JavaConverters._

/**
  * UI model for a mapping config. All fields with complex mutable values are
  * immutable. Simple fields or fields with immutable values (like a String
  * fields) are mutable in this config.
  * <p>
  * There may be several instances of [[UISyncConfig]] for a same
  * "hard-copy". Moreover, that instances may differs from each other. Users of
  * this class should be aware of this behavior.
  *
  */
object UISyncConfig {

  private def reverse(fieldMappings: Seq[FieldMapping]): Seq[FieldMapping] = {
    fieldMappings.map(reverse)
  }

  private def reverse(mapping: FieldMapping) = FieldMapping(mapping.fieldInConnector2, mapping.fieldInConnector1, mapping.selected, mapping.defaultValue)


  /**
    * Loads tasks from connector1. This is invoked directly from UI layer when user clicks "Go" to load tasks.
    * This method also decorates loaded tasks with "remote Ids" for known tasks (as defined by "tasks cache")
    *
    * @param taskLimit max number of tasks to load.
    * @return loaded tasks.
    */
  @throws[ConnectorException]
  def loadTasks(config: UISyncConfig, taskLimit: Int): util.List[GTask] = {
    val loadedTasks = TaskLoader.loadTasks(taskLimit, config.getConnector1.createConnectorInstance,
      config.getConnector1.getLabel, ProgressMonitorUtils.DUMMY_MONITOR)
    loadedTasks
  }

}

case class UISyncConfig(configRootFolder: File,

                        /**
                          * Config identity. Unique "config-storage" id to distinguish between
                          * configs. May be <code>null</code> for a new (non-saved) config.
                          */
                        identity: String,

                        /** Name of the user who owns this config. */
                        owner: String,

                        /**
                          * Config label
                          */
                        @BeanProperty label: String,

                        /**
                          * First connector config.
                          */
                        connector1: UIConnectorConfig,

                        /**
                          * Second connector config.
                          */
                        connector2: UIConnectorConfig,

                        /**
                          * Field mappings. Left side is connector1, right side is connector2.
                          */
                        fieldMappings: Seq[FieldMapping],

                        /**
                          * "Config is reversed" flag.
                          */
                        var reversed: Boolean,

                        schedule: Schedule
                       ) {

  val id = ConfigId(owner, identity)

  def getConnector1: UIConnectorConfig = connector1

  def getConnector2: UIConnectorConfig = connector2

  def getNewMappings: Seq[FieldMapping] = fieldMappings

  /** Returns name of the user who owns this config. */
  def getOwnerName: String = owner

  /**
    * Creates a "reversed" version of a config. Reversed version shares
    * connector configurations and config identity with this config, but have
    * new mapping instance.
    *
    * @return "reversed" (back-order) configuration.
    */
  def reverse = new UISyncConfig(configRootFolder, identity, owner, label, connector2, connector1,
    UISyncConfig.reverse(fieldMappings), !reversed,
    schedule)

  /**
    * Returns a "normalized" (canonical) form of this config.
    *
    * @return normalized (canonical) version of this config.
    */
  def normalized: UISyncConfig = if (reversed) reverse
  else this

  /**
    * Checks, if config is reversed (i.e. connector1/connector2 are opposite
    * from a stored data).
    *
    * @return <code>true</code> iff config is reversed.
    */
  def isReversed: Boolean = reversed

  /**
    * Generates a source mappings. Returned mappings is snapshot of a current
    * state and are not updated when newMappings changes.
    *
    * @return source mappings
    */
  private def generateFieldRowsToExportLeft = MappingBuilder.build(fieldMappings, ExportDirection.LEFT)

  private def generateFieldRowsToExportRight = MappingBuilder.build(fieldMappings, ExportDirection.RIGHT)

  def getPreviouslyCreatedTasksResolver(): PreviouslyCreatedTasksResolver = {
    val location1 = getConnector1.getSourceLocation
    val location2 = getConnector2.getSourceLocation
    TaskKeeperLocationStorage.loadTasks(configRootFolder, location1, location2)
  }

  def saveTasks(tasks: util.List[GTask], progressMonitor: ProgressMonitor): ExportResultFormat = {
    val start = System.currentTimeMillis()
    val connectorTo = getConnector2.createConnectorInstance
    val destinationLocation = getConnector2.getDestinationLocation
    val rows = generateFieldRowsToExportRight

    val location1 = getConnector1.getSourceLocation
    val location2 = getConnector2.getSourceLocation
    val previouslyCreatedTasksResolver = TaskKeeperLocationStorage.loadTasks(configRootFolder, location1, location2)
    val result = TaskSaver.save(previouslyCreatedTasksResolver, connectorTo, destinationLocation, rows, tasks, progressMonitor)
    TaskKeeperLocationStorage.store(configRootFolder, location1, location2, result.keyToRemoteKeyList)
    val finish = System.currentTimeMillis()

    val finalResult = ExportResultFormat(id, label, getConnector1.getSourceLocation, destinationLocation,
      Option(result.targetFileAbsolutePath),
      result.updatedTasksNumber, result.createdTasksNumber,
      result.generalErrors.map(getConnector2.decodeException),
      result.taskErrors.map(e =>
        (e.getTask.getSourceSystemId, getConnector2.decodeException(e.getError))
      ),
      new Date(start),
      ((finish - start) / 1000).toInt
    )
    finalResult
  }

  @throws[ConnectorException]
  def loadTasksForUpdate(): util.List[GTask] = {
    val updater = makeUpdater
    updater.loadTasks()
    updater.removeTasksWithoutRemoteIds()
    updater.getExistingTasks
  }

  private def makeUpdater = {
    val sourceConnector = getConnector1.createConnectorInstance
    val destinationConnector = getConnector2.createConnectorInstance
    val updater = new Updater(destinationConnector, generateFieldRowsToExportRight.asJava, sourceConnector, getConnector1.getDestinationLocation)
    updater
  }

  /**
    * @return number of updated tasks.
    */
  @throws[ConnectorException]
  def updateTasks(selectedTasks: util.List[GTask], progress: ProgressMonitor): Int = {
    val updater = makeUpdater
    updater.setConfirmedTasks(selectedTasks)
    updater.setMonitor(progress)
    updater.loadExternalTasks()
    updater.saveFile()
    updater.getNumberOfUpdatedTasks
  }

  @throws[ConnectorException]
  def loadDropInTasks(tempFile: File, taskLimit: Int): util.List[GTask] = {
    TaskLoader.loadDropInTasks(taskLimit,
      getConnector1.createConnectorInstance.asInstanceOf[DropInConnector],
      tempFile, ProgressMonitorUtils.DUMMY_MONITOR)
  }

}
