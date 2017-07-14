package com.taskadapter.web.uiapi

import java.util

import com.taskadapter.config.CirceBoilerplateForConfigs._
import com.taskadapter.config.{ConfigStorage, StorageException, StoredExportConfig}
import com.taskadapter.core.TaskKeeper
import io.circe.generic.auto._
import io.circe.syntax._

import scala.collection.JavaConverters._

/**
  * UI-level config manager. Manages UIMappingConfigs instead of low-level
  * {@link StoredConnectorConfig}. All methods of this class creates new fresh
  * instances of UIMappingConfig. Modifications of that instances will not affect
  * other instances for a same config file. See also documentation for
  * {@link UISyncConfig}.
  */
class UIConfigStore(taskKeeper:TaskKeeper, uiConfigService: UIConfigService, configStorage: ConfigStorage) {
  val syncConfigBuilder = new UISyncConfigBuilder(taskKeeper, uiConfigService)

  /**
    * Lists all user-created configs.
    *
    * @param userLoginName login name to load items for.
    * @return collection of the user's config in no particular order.
    */
  def getUserConfigs(userLoginName: String): util.List[UISyncConfig] = {
    val storedConfigs: util.List[StoredExportConfig] = configStorage.getUserConfigs(userLoginName)
    storedConfigs.asScala
      .map(storedConfig => syncConfigBuilder.uize(userLoginName, storedConfig))
      .asJava
  }

  /**
    * Creates a new (fresh) config.
    *
    * @param userName     user login name (for whom config will be created).
    * @param label        config label (name).
    * @param connector1id first connector id.
    * @param connector2id second connector id.
    * @return newly created (and saved) UI mapping config.
    */
  @throws[StorageException]
  def createNewConfig(userName: String, label: String, connector1id: String, connector2id: String): UISyncConfig = {
    val config1: UIConnectorConfig = uiConfigService.createDefaultConfig(connector1id)
    val config2: UIConnectorConfig = uiConfigService.createDefaultConfig(connector2id)
    val newMappings = NewConfigSuggester.suggestedFieldMappingsForNewConfig(config1.getSuggestedCombinations,
      config2.getSuggestedCombinations)
    val mappingsString = newMappings.asJson.noSpaces
    val identity: String = configStorage.createNewConfig(userName, label,
      config1.getConnectorTypeId, config1.getConfigString,
      config2.getConnectorTypeId, config2.getConfigString, mappingsString)
    new UISyncConfig(taskKeeper, identity, userName, label, config1, config2, newMappings.asJava, false)
  }

  /**
    * Saves a config.
    *
    * @param syncConfig config to save.
    * @throws StorageException if config cannot be saved.
    */
  @throws[StorageException]
  def saveConfig(syncConfig: UISyncConfig): Unit = {
    val normalizedSyncConfig = syncConfig.normalized
    val label: String = normalizedSyncConfig.getLabel
    val config1: UIConnectorConfig = normalizedSyncConfig.getConnector1
    val config2: UIConnectorConfig = normalizedSyncConfig.getConnector2
    val mappings = normalizedSyncConfig.getNewMappings.asScala
    val mappingsStr = mappings.asJson.noSpaces

    configStorage.saveConfig(normalizedSyncConfig.getOwnerName, normalizedSyncConfig.getIdentity, label,
      config1.getConnectorTypeId, config1.getConfigString, config2.getConnectorTypeId, config2.getConfigString, mappingsStr)
  }

  def deleteConfig(config: UISyncConfig): Unit = {
    configStorage.delete(config.getIdentity)
  }

  /**
    * @param userLoginName clone owner's login name.
    * @param syncConfig    config to clone.
    */
  @throws[StorageException]
  def cloneConfig(userLoginName: String, syncConfig: UISyncConfig): Unit = {
    val label: String = syncConfig.getLabel
    val config1 = syncConfig.getConnector1
    val config2 = syncConfig.getConnector2
    val mappings = syncConfig.getNewMappings.asScala
    val mappingsStr = mappings.asJson.noSpaces
    configStorage.createNewConfig(userLoginName, label, config1.getConnectorTypeId, config1.getConfigString,
      config2.getConnectorTypeId, config2.getConfigString, mappingsStr)
  }
}
