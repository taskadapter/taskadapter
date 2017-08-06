package com.taskadapter.web.uiapi

import java.io.File

import com.taskadapter.config.CirceBoilerplateForConfigs._
import com.taskadapter.config._
import com.taskadapter.connector.NewConfigSuggester
import com.taskadapter.connector.common.XorEncryptor
import com.taskadapter.connector.definition._
import io.circe.Json
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

/**
  * UI-level config manager. Manages UIMappingConfigs instead of low-level
  * [[com.taskadapter.config.StoredConnectorConfig]]. All methods of this class creates new fresh
  * instances of UIMappingConfig. Modifications of that instances will not affect
  * other instances for a same config file. See also documentation for
  * [[UISyncConfig]].
  */
class UIConfigStore(uiConfigService: UIConfigService, configStorage: ConfigStorage) {
  private val logger = LoggerFactory.getLogger(classOf[ConfigStorage])

  val encryptor = new XorEncryptor

  /**
    * Lists all user-created configs.
    *
    * @param userLoginName login name to load items for.
    * @return collection of the user's config in no particular order.
    */
  def getUserConfigs(userLoginName: String): Seq[UISyncConfig] = {
    val storedConfigs = configStorage.getUserConfigs(userLoginName)
    storedConfigs.map(storedConfig => uize(userLoginName, storedConfig))
  }

  def getSavedSetupsFolder(loginName: String): File = {
    configStorage.getUserFolder(loginName)
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

    val config1 = uiConfigService.createRichConfig(conn1Config.connectorTypeId, conn1Config.serializedConfig)
    val config2 = uiConfigService.createRichConfig(conn2Config.connectorTypeId, conn2Config.serializedConfig)
    val jsonString = storedConfig.getMappingsString

    val connector1Setup = loadSetup(ownerName, conn1Config.connectorSavedSetupId)
    val connector2Setup = loadSetup(ownerName, conn2Config.connectorSavedSetupId)

    config1.setConnectorSetup(connector1Setup)
    config2.setConnectorSetup(connector2Setup)

    val newMappings = decode[Seq[FieldMapping]](jsonString)
    newMappings match {
      case Left(e) => throw new RuntimeException(s"cannot parse mappings from config $storedConfig: $e")
      case Right(m) =>
        new UISyncConfig(configStorage.rootDir, storedConfig.getId, ownerName, label, config1, config2, m, false)
    }
  }

  /**
    * Creates a new (fresh) config.
    *
    * @param userName     user login name (for whom config will be created).
    * @param label        config label (name).
    * @param connector1Id first connector id.
    * @param connector2Id second connector id.
    * @return newly created (and saved) UI mapping config.
    */
  @throws[StorageException]
  def createNewConfig(userName: String, label: String, connector1Id: String, connector1Label: String,
                      connector2Id: String, connector2Label: String): ConfigId = {
    val config1 = uiConfigService.createDefaultConfig(connector1Id)
    config1.setLabel(connector1Label)

    val config2 = uiConfigService.createDefaultConfig(connector2Id)
    config2.setLabel(connector2Label)

    val newMappings = NewConfigSuggester.suggestedFieldMappingsForNewConfig(
      config1.getSuggestedCombinations,
      config2.getSuggestedCombinations)
    val mappingsString = newMappings.asJson.noSpaces
    val configId = configStorage.createNewConfig(userName, label,
      config1.getConnectorTypeId, config1.getLabel, config1.getConfigString,
      config2.getConnectorTypeId, config2.getLabel, config2.getConfigString,
      mappingsString)

    configId
  }

  def saveSetup(userName: String, setup: ConnectorSetup, label: String): Unit = {
    val jsonString: String = if (setup.isInstanceOf[WebConnectorSetup]) {
      val webSetup: WebConnectorSetup = setup.asInstanceOf[WebConnectorSetup]
      webSetup.copy(password = encryptor.encrypt(webSetup.password),
        apiKey = encryptor.encrypt(webSetup.apiKey)
      ).asJson.spaces2
    } else {
      // only file setup is left possible
      val fileSetup: FileSetup = setup.asInstanceOf[FileSetup]
      fileSetup.asJson.spaces2
    }
    configStorage.saveConnectorSetup(userName, label, jsonString)
  }

  def loadSetup(userName: String, setupLabel: String): ConnectorSetup = {
    val string = configStorage.loadConnectorSetupAsString(userName, setupLabel)
    val json = parseSetupStringToJson(string, userName).get

    val maybeParsedConfig = convertJsonSetupsToConnectorSetups(Seq(json)).head
    if (maybeParsedConfig.isDefined) {
      maybeParsedConfig.get
    } else {
      throw new RuntimeException(s"Cannot parse config $json")
    }
  }

  def getAllConnectorSetups(userLoginName: String, connectorId: String): Seq[ConnectorSetup] = {
    val setups: Seq[Option[Json]] = configStorage.getAllConnectorSetupsAsStrings(userLoginName, connectorId).asScala
      .map { setupString =>
        parseSetupStringToJson(setupString, userLoginName)
      }
    val setupsForThisConnectorId = setups.filter(maybeSetup => maybeSetup.isDefined
      && maybeSetup.get.hcursor.get[String]("connectorId").toOption.contains(connectorId)).flatten

    convertJsonSetupsToConnectorSetups(setupsForThisConnectorId).flatten
  }

  def convertJsonSetupsToConnectorSetups(jsonSeq: Seq[Json]): Seq[Option[ConnectorSetup]] = {
    jsonSeq.map { s =>
      if (s.hcursor.get[String]("connectorId").toOption.contains("Microsoft Project")) {
        s.as[FileSetup] match {
          case Left(e) => logger.error(s"Cannot parse connector setup $s as FileSetup $e")
            None
          case Right(setup) => Some(setup)
        }
      } else {
        s.as[WebConnectorSetup] match {
          case Left(e) => logger.error(s"Cannot parse connector setup $s as WebConnectorSetup $e")
            None
          case Right(setup) => Some(setup.copy(password = encryptor.decrypt(setup.password),
            apiKey = encryptor.decrypt(setup.apiKey)))
        }
      }
    }
  }

  /**
    * @return json map because this may contains [[WebConnectorSetup]] or [[FileSetup]] or whatever.
    */
  def parseSetupStringToJson(string: String, userName: String): Option[Json] = {
    decode[Json](string) match {
      case Left(e) => logger.error(s"Cannot parse connector setup for user $userName. $e")
        None
      case Right(setup) => Some(setup)
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
    val config1 = normalizedSyncConfig.getConnector1
    val config2 = normalizedSyncConfig.getConnector2
    val mappings = normalizedSyncConfig.getNewMappings
    val mappingsStr = mappings.asJson.noSpaces

    configStorage.saveConfig(normalizedSyncConfig.getOwnerName, normalizedSyncConfig.identity, label,
      config1.getConnectorTypeId, config1.getConnectorSetup.label, config1.getConfigString,
      config2.getConnectorTypeId, config2.getConnectorSetup.label, config2.getConfigString,
      mappingsStr)
  }

  def deleteConfig(configId: ConfigId): Unit = {
    configStorage.delete(configId)
  }

  /**
    * @param userLoginName name of the new config owner.
    * @param configId unique identifier for config to clone
    */
  @throws[StorageException]
  def cloneConfig(userLoginName: String, configId: ConfigId): Unit = {
    val savedConfig = configStorage.getConfig(configId)
    savedConfig match {
      case Some(config) =>
        val connector1 = config.getConnector1
        val connector2 = config.getConnector2
        configStorage.createNewConfig(userLoginName, config.getName,
          connector1.connectorTypeId, connector1.connectorSavedSetupId, connector1.serializedConfig,
          connector2.connectorTypeId, connector2.connectorSavedSetupId, connector2.connectorTypeId,
          config.getMappingsString)
      case None => throw new StorageException(s"Cannot find config with id $configId to clone")
    }
  }
}
