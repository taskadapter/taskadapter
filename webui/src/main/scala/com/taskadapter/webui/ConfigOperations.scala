package com.taskadapter.webui

import java.io.File
import java.util

import com.taskadapter.auth.{AuthorizedOperations, CredentialsManager}
import com.taskadapter.config.StorageException
import com.taskadapter.connector.definition.ConnectorSetup
import com.taskadapter.web.uiapi.{ConfigId, SetupId, UIConfigStore, UISyncConfig}
import com.taskadapter.webui.results.{ExportResultFormat, ExportResultStorage}

import scala.collection.JavaConverters._
import scala.collection.Seq

final class ConfigOperations(/**
                               * Name of the owner.
                               */
                             val userName: String,

                             /**
                               * User permissions.
                               */
                             val authorizedOps: AuthorizedOperations,

                             /**
                               * Credentials manager.
                               */
                             val credManager: CredentialsManager,

                             /**
                               * Config accessor.
                               */
                             val uiConfigStore: UIConfigStore,

                             /**
                               * Synchronization sandbox.
                               */
                             val syncSandbox: File) {
  /**
    * @return list of configs that user owns.
    */
  def getOwnedConfigs: util.List[UISyncConfig] = uiConfigStore.getUserConfigs(userName).asJava

  def getSavedSetupsFolder = uiConfigStore.getSavedSetupsFolder(userName)
  def getConfig(configId: ConfigId): Option[UISyncConfig] = uiConfigStore.getConfig(configId)

  /**
    * @return list of configs that user can manage.
    */
  def getManageableConfigs: util.List[UISyncConfig] = {
    if (!authorizedOps.canManagerPeerConfigs) return getOwnedConfigs
    val res = new util.ArrayList[UISyncConfig]
    credManager.listUsers.asScala.foreach(user =>
      res.addAll(uiConfigStore.getUserConfigs(user).asJava)
    )
    res
  }

  def getExportResults(rootFolder: File, configId: ConfigId): Seq[ExportResultFormat] = {
    ExportResultStorage.getSaveResults(rootFolder, configId)
  }

  /**
    * Creates a new config and returns it.
    *
    * @param descriptionString config description.
    * @param connector1Id      first connector id.
    * @param connector2Id      second connector id.
    * @return new config
    * @throws StorageException if config cannot be created.
    */
  @throws[StorageException]
  def createNewConfig(descriptionString: String, connector1Id: String, connector1SetupId: SetupId,
                      connector2Id: String, connector2SetupId: SetupId): ConfigId =
  uiConfigStore.createNewConfig(userName, descriptionString, connector1Id, connector1SetupId, connector2Id, connector2SetupId)

  /**
    * Delete a config.
    *
    * @param configIdentity a unique id for the config in the store
    */
  def deleteConfig(configIdentity: ConfigId): Unit = {
    uiConfigStore.deleteConfig(configIdentity)
  }

  /**
    * Clones config. Current user became the owner of the clone.
    *
    * @param configId a unique id the config in the store
    * @throws StorageException if config cannot be cloned.
    */
  @throws[StorageException]
  def cloneConfig(configId: ConfigId): Unit = {
    uiConfigStore.cloneConfig(userName, configId)
  }

  @throws[StorageException]
  def saveConfig(config: UISyncConfig): Unit = {
    uiConfigStore.saveConfig(config)
  }

  def saveNewSetup(setup: ConnectorSetup): SetupId =
    uiConfigStore.saveNewSetup(userName, setup)

  def saveSetup(setup: ConnectorSetup, id: SetupId): Unit =
    uiConfigStore.saveSetup(userName, setup, id)

  def getAllConnectorSetups(connectorId: String): Seq[ConnectorSetup] =
    uiConfigStore.getAllConnectorSetups(userName, connectorId)

  def getConnectorSetups(): Seq[ConnectorSetup] =
    uiConfigStore.getAllConnectorSetups(userName)

  def getSetup(setupId: SetupId): ConnectorSetup =
    uiConfigStore.getSetup(userName, setupId)

  def deleteConnectorSetup(id: SetupId): Unit = {
    uiConfigStore.deleteSetup(userName, id)
  }

  def getConfigIdsUsingThisSetup(id: SetupId) : Seq[ConfigId] = {
    uiConfigStore.getConfigIdsUsingThisSetup(userName, id)
  }
}
