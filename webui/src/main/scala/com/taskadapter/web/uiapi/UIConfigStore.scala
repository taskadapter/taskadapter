package com.taskadapter.web.uiapi

import java.util

import com.taskadapter.config.CirceBoilerplateForConfigs._
import com.taskadapter.config.{ConfigStorage, ConnectorSetup, StorageException, StoredExportConfig}
import com.taskadapter.connector.NewConfigSuggester
import com.taskadapter.connector.common.XorEncryptor
import com.taskadapter.connector.definition.{FieldMapping, WebServerInfo}
import com.taskadapter.core.TaskKeeper
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.parser._
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._

/**
  * UI-level config manager. Manages UIMappingConfigs instead of low-level
  * [[com.taskadapter.config.StoredConnectorConfig]]. All methods of this class creates new fresh
  * instances of UIMappingConfig. Modifications of that instances will not affect
  * other instances for a same config file. See also documentation for
  * [[UISyncConfig]].
  */
class UIConfigStore(taskKeeper: TaskKeeper, uiConfigService: UIConfigService, configStorage: ConfigStorage) {
  private val logger = LoggerFactory.getLogger(classOf[ConfigStorage])

  val encryptor = new XorEncryptor
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
      .map(storedConfig => uize(userLoginName, storedConfig))
      .asJava
  }

  /**
    * Create a new UI config instance for a stored config.
    *
    * @param ownerName    name of config owner.
    * @param storedConfig stored config to create an instance for.
    * @return new parsed config.
    */
  def uize(ownerName: String, storedConfig: StoredExportConfig): UISyncConfig = {
    val label = storedConfig.getName
    val conn1Config = storedConfig.getConnector1
    val conn2Config = storedConfig.getConnector2

    val config1 = uiConfigService.createRichConfig(conn1Config.getConnectorTypeId, conn1Config.getSerializedConfig)
    val config2 = uiConfigService.createRichConfig(conn2Config.getConnectorTypeId, conn2Config.getSerializedConfig)
    val jsonString = storedConfig.getMappingsString

    val webServerInfo1 = loadSetup(ownerName, config1.getLabel)
    val webServerInfo2 = loadSetup(ownerName, config2.getLabel)

    config1.setWebServerInfo(webServerInfo1)
    config2.setWebServerInfo(webServerInfo2)

    val newMappings = decode[Seq[FieldMapping]](jsonString)
    newMappings match {
      case Left(e) => throw new RuntimeException(s"cannot parse mappings from config $storedConfig: $e")
      case Right(m) =>
        new UISyncConfig(taskKeeper, storedConfig.getId, ownerName, label, config1, config2, m.asJava, false)
    }
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
  def createNewConfig(userName: String, label: String, connector1id: String, connector2id: String,
                      connector1Info: WebServerInfo, connector2Info: WebServerInfo): UISyncConfig = {
    val config1: UIConnectorConfig = uiConfigService.createDefaultConfig(connector1id)
    val config2: UIConnectorConfig = uiConfigService.createDefaultConfig(connector2id)
    val newMappings = NewConfigSuggester.suggestedFieldMappingsForNewConfig(
      config1.getSuggestedCombinations,
      config2.getSuggestedCombinations)
    val mappingsString = newMappings.asJson.noSpaces
    val identity: String = configStorage.createNewConfig(userName, label,
      config1.getConnectorTypeId, config1.getConfigString,
      config2.getConnectorTypeId, config2.getConfigString, mappingsString)

    saveSetup(userName, connector1Info)
    saveSetup(userName, connector2Info)
    new UISyncConfig(taskKeeper, identity, userName, label, config1, config2, newMappings.asJava, false)
  }

  def saveSetup(userName: String, setup: WebServerInfo): Unit = {
    configStorage.saveConnectorSetup(userName,
      setup.getLabel,
      ConnectorSetup(setup.getLabel, setup.getHost, setup.getUserName,
        encryptor.encrypt(setup.getPassword),
        setup.isUseAPIKeyInsteadOfLoginPassword,
        encryptor.encrypt(setup.getApiKey)
      ).asJson.spaces2)
  }

  def loadSetup(userName: String, setupLabel: String): WebServerInfo = {
    val string = configStorage.loadConnectorSetupAsString(userName, setupLabel)
    decode[ConnectorSetup](string) match {
      case Left(e) => logger.error(s"Cannot parse connector setup for user $userName, setup label $setupLabel. $e")
        null
      case Right(setup) => new WebServerInfo(setup.label, setup.host, setup.userName,
        encryptor.decrypt(setup.password),
        setup.useApiKey,
        encryptor.decrypt(setup.apiKey))
    }
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
