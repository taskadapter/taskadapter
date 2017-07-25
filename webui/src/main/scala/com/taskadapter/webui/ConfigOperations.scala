package com.taskadapter.webui

import java.io.File
import java.util

import com.taskadapter.auth.{AuthorizedOperations, CredentialsManager}
import com.taskadapter.config.{ConnectorSetup, StorageException}
import com.taskadapter.connector.definition.WebServerInfo
import com.taskadapter.web.uiapi.{UIConfigStore, UISyncConfig}

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
  def getOwnedConfigs: util.List[UISyncConfig] = uiConfigStore.getUserConfigs(userName)

  /**
    * @return list of configs that user can manage.
    */
  def getManageableConfigs: util.List[UISyncConfig] = {
    if (!authorizedOps.canManagerPeerConfigs) return getOwnedConfigs
    val res = new util.ArrayList[UISyncConfig]
    credManager.listUsers.asScala.foreach(user =>
      res.addAll(uiConfigStore.getUserConfigs(user))
    )
    res
  }

  /**
    * Creates a new config and returns it.
    *
    * @param descriptionString config description.
    * @param connector1Id               first connector id.
    * @param connector2Id               second connector id.
    * @return new config
    * @throws StorageException if config cannot be created.
    */
  @throws[StorageException]
  def createNewConfig(descriptionString: String, connector1Id: String, connector1Label:String,
                      connector2Id: String, connector2Label: String): UISyncConfig =
  uiConfigStore.createNewConfig(userName, descriptionString, connector1Id, connector1Label, connector2Id, connector2Label)

  /**
    * Deletes a config.
    *
    * @param config
    * config to delete.
    */
  def deleteConfig(config: UISyncConfig): Unit = {
    uiConfigStore.deleteConfig(config)
  }

  /**
    * Clones config. Current user became the owner of the clone.
    *
    * @param config config to clone.
    * @throws StorageException if config cannot be cloned.
    */
  @throws[StorageException]
  def cloneConfig(config: UISyncConfig): Unit = {
    uiConfigStore.cloneConfig(userName, config)
  }

  @throws[StorageException]
  def saveConfig(config: UISyncConfig): Unit = {
    uiConfigStore.saveConfig(config)
  }

  def saveSetup(setup: WebServerInfo, connectorId: String): Unit =
    uiConfigStore.saveSetup(userName, setup, connectorId)

  def getAllConnectorSetups(connectorId: String): Seq[ConnectorSetup] = uiConfigStore.getAllConnectorSetups(userName, connectorId)
}
