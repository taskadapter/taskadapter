package com.taskadapter.web.uiapi

import java.io.File
import java.util

import com.taskadapter.connector.MappingBuilder
import com.taskadapter.connector.common.ProgressMonitorUtils
import com.taskadapter.connector.definition.exceptions.ConnectorException
import com.taskadapter.connector.definition.{ExportDirection, FieldMapping, ProgressMonitor, SaveResult}
import com.taskadapter.core._
import com.taskadapter.model.GTask
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

  private def reverse(fieldMappings: util.List[FieldMapping]): util.ArrayList[FieldMapping] = {
    val result = new util.ArrayList[FieldMapping]
    import scala.collection.JavaConversions._
    for (mapping <- fieldMappings) {
      result.add(reverse(mapping))
    }
    result
  }

  private def reverse(mapping: FieldMapping) = FieldMapping(mapping.fieldInConnector2, mapping.fieldInConnector1, mapping.selected, mapping.defaultValue)
}

final class UISyncConfig(configRootFolder: File, taskKeeper: TaskKeeper,

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
                         var label: String,

                         /**
                           * First connector config.
                           */
                         val connector1: UIConnectorConfig,

                         /**
                           * Second connector config.
                           */
                         val connector2: UIConnectorConfig,

                         /**
                           * Field mappings. Left side is connector1, right side is connector2.
                           */
                         val fieldMappings: util.List[FieldMapping],

                         /**
                           * "Config is reversed" flag.
                           */
                         var reversed: Boolean) {
  def setLabel(label: String): Unit = {
    this.label = label
  }

  def getLabel: String = label

  def getConnector1: UIConnectorConfig = connector1

  def getConnector2: UIConnectorConfig = connector2

  def getNewMappings: util.List[FieldMapping] = fieldMappings

  /** Returns name of the user who owns this config. */
  def getOwnerName: String = owner

  private[uiapi] def getIdentity = identity

  /**
    * Creates a "reversed" version of a config. Reversed version shares
    * connector configurations and config identity with this config, but have
    * new mapping instance.
    *
    * @return "reversed" (back-order) configuration.
    */
  def reverse = new UISyncConfig(configRootFolder, taskKeeper, identity, owner, label, connector2, connector1, UISyncConfig.reverse(fieldMappings), !reversed)

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
  private[uiapi] def generateFieldRowsToExportLeft = MappingBuilder.build(fieldMappings, ExportDirection.LEFT)

  private[uiapi] def generateFieldRowsToExportRight = MappingBuilder.build(fieldMappings, ExportDirection.RIGHT)

  /**
    * Loads tasks from connector1. This is invoked directly from UI layer when user clicks "Go" to load tasks.
    * This method also decorates loaded tasks with "remote Ids" for known tasks (as defined by "tasks cache")
    *
    * @param taskLimit max number of tasks to load.
    * @return loaded tasks.
    */
  @throws[ConnectorException]
  def loadTasks(taskLimit: Int): util.List[GTask] = {
    val loadedTasks = TaskLoader.loadTasks(taskLimit, getConnector1.createConnectorInstance, getConnector1.getLabel, ProgressMonitorUtils.DUMMY_MONITOR)
    val map = loadPreviouslyCreatedTasks()
    loadedTasks.asScala.foreach(t => t.setRemoteId(map.getOrElse(t.getKey, "")+""))
    loadedTasks
  }

  private def loadPreviouslyCreatedTasks(): Map[String, Long] = {
    val location1 = getConnector1.getSourceLocation
    val location2 = getConnector2.getSourceLocation
    TaskKeeperLocationStorage.loadTasks(configRootFolder, location1, location2)
  }

  @throws[ConnectorException]
  def loadDropInTasks(tempFile: File, taskLimit: Int): util.List[GTask] = { // TODO TA3 drag-n-drop
    throw new RuntimeException("not implemented")
    /*
            return TaskLoader.loadDropInTasks(taskLimit,
                    (DropInConnector) getConnector1().createConnectorInstance(),
                    tempFile, generateFieldRowsToExportLeft(),
                    ProgressMonitorUtils.DUMMY_MONITOR);
    */
  }

  def saveTasks(tasks: util.List[GTask], progressMonitor: ProgressMonitor): SaveResult = {
    val connectorTo = getConnector2.createConnectorInstance
    val destinationLocation = getConnector2.getDestinationLocation
    val rows = generateFieldRowsToExportRight

    val location1 = getConnector1.getSourceLocation
    val location2 = getConnector2.getSourceLocation
    val previouslyCreatedTasks = TaskKeeperLocationStorage.loadTasks(configRootFolder, location1, location2)
    val result = TaskSaver.save(previouslyCreatedTasks, connectorTo, destinationLocation, rows, tasks, progressMonitor)
    TaskKeeperLocationStorage.store(configRootFolder, location1, location2, result.getIdToRemoteKeyList)
    result
  }

  @throws[ConnectorException]
  def loadTasksForUpdate: util.List[GTask] = {
    val updater = makeUpdater
    updater.loadTasks()
    updater.removeTasksWithoutRemoteIds()
    updater.getExistingTasks
  }

  private def makeUpdater = {
    val sourceConnector = getConnector1.createConnectorInstance
    val destinationConnector = getConnector2.createConnectorInstance
    val updater = new Updater(destinationConnector, generateFieldRowsToExportRight, sourceConnector, getConnector1.getDestinationLocation)
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
}
