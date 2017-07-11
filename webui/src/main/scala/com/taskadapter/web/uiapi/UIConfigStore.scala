package com.taskadapter.web.uiapi

import java.util

import com.google.gson.Gson
import com.taskadapter.config.{CirceBoilerplateForConfigs, ConfigStorage, StorageException, StoredExportConfig}
import com.taskadapter.connector.definition.FieldMapping

import com.taskadapter.config.CirceBoilerplateForConfigs._

import scala.collection.JavaConverters._
import io.circe.parser._
import io.circe._
import io.circe.generic.semiauto._
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

/**
  * UI-level config manager. Manages UIMappingConfigs instead of low-level
  * {@link StoredConnectorConfig}. All methods of this class creates new fresh
  * instances of UIMappingConfig. Modifications of that instances will not affect
  * other instances for a same config file. See also documentation for
  * {@link UISyncConfig}.
  */
class UIConfigStore(uiConfigService: UIConfigService, configStorage: ConfigStorage) {
  val syncConfigBuilder = new UISyncConfigBuilder(uiConfigService)

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
    val mappingsString: String = new Gson().toJson(newMappings)
    val identity: String = configStorage.createNewConfig(userName, label,
      config1.getConnectorTypeId, config1.getConfigString,
      config2.getConnectorTypeId, config2.getConfigString, mappingsString)
    new UISyncConfig(identity, userName, label, config1, config2, newMappings.asJava, false)
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
    val config1: UIConnectorConfig = syncConfig.getConnector1
    val config2: UIConnectorConfig = syncConfig.getConnector2
    val mappings: util.List[FieldMapping] = syncConfig.getNewMappings
    val mappingsStr: String = new Gson().toJson(mappings)
    configStorage.createNewConfig(userLoginName, label, config1.getConnectorTypeId, config1.getConfigString,
      config2.getConnectorTypeId, config2.getConfigString, mappingsStr)
  }
}
