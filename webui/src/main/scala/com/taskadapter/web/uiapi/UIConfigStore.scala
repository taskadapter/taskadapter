package com.taskadapter.web.uiapi

import java.io.File

import com.taskadapter.auth.cred.CredentialsStore
import com.taskadapter.config._
import com.taskadapter.connector.NewConfigSuggester
import com.taskadapter.connector.common.{FileNameGenerator, XorEncryptor}
import com.taskadapter.connector.definition._
import com.taskadapter.web.TaskKeeperLocationStorage
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
class UIConfigStore(uiConfigService: UIConfigService, configStorage: ConfigStorage, usersStorage: CredentialsStore) {
  private val logger = LoggerFactory.getLogger(classOf[ConfigStorage])

  val encryptor = new XorEncryptor

  def getConfigs(): Seq[UISyncConfig] = {
    usersStorage.listUsers().asScala
      .map(u=> getUserConfigs(u)).flatten
  }

  /**
    * Lists all user-created configs.
    *
    * @param userLoginName login name to load items for.
    * @return collection of the user's config in no particular order.
    */
  def getUserConfigs(userLoginName: String): Seq[UISyncConfig] = {
    val storedConfigs = configStorage.getUserConfigs(userLoginName)
    storedConfigs.flatMap(storedConfig => try {
      Some(uize(userLoginName, storedConfig))
    } catch {
      case e: Exception =>
        logger.error(s"Error parsing config ${storedConfig.getId} for user $userLoginName. Skipping this config. $e")
        None
    })
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

    val connector1Setup = getSetup(ownerName, conn1Config.connectorSavedSetupId)
    val connector2Setup = getSetup(ownerName, conn2Config.connectorSavedSetupId)

    config1.setConnectorSetup(connector1Setup)
    config2.setConnectorSetup(connector2Setup)

    try {
      val newMappings = JsonFactory.fromJsonString(jsonString)
      new UISyncConfig(new TaskKeeperLocationStorage(configStorage.rootDir), storedConfig.getId, ownerName,
        label, config1, config2, newMappings, false)
    } catch {
      case e: Exception =>
        throw new RuntimeException(s"cannot parse mappings from config $storedConfig: $e")
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
  def createNewConfig(userName: String, label: String, connector1Id: String, connector1SetupId: SetupId,
                      connector2Id: String, connector2SetupId: SetupId): ConfigId = {
    val config1 = uiConfigService.createDefaultConfig(connector1Id)
    val config2 = uiConfigService.createDefaultConfig(connector2Id)

    val newMappings = NewConfigSuggester.suggestedFieldMappingsForNewConfig(
      config1.getDefaultFieldsForNewConfig,
      config2.getDefaultFieldsForNewConfig)
    val mappingsString = JsonFactory.toString(newMappings)
    val configId = configStorage.createNewConfig(userName, label,
      config1.getConnectorTypeId, connector1SetupId, config1.getConfigString,
      config2.getConnectorTypeId, connector2SetupId, config2.getConfigString,
      mappingsString)

    configId
  }

  def getConfig(configId: ConfigId): Option[UISyncConfig] = getUserConfigs(configId.ownerName).find(_.id == configId)

  def saveNewSetup(userName: String, setup: ConnectorSetup): SetupId = {
    val newFile = FileNameGenerator.findSafeAvailableFileName(getSavedSetupsFolder(userName), setup.connectorId + "_%d.json")
    val setupId = SetupId(newFile.getName)
    saveSetup(userName, setup, setupId)
    setupId
  }

  def saveSetup(userName: String, setup: ConnectorSetup, setupId: SetupId): Unit = {
    val jsonString: String = if (setup.isInstanceOf[WebConnectorSetup]) {
      val webSetup: WebConnectorSetup = setup.asInstanceOf[WebConnectorSetup]
      webSetup.copy(id = Some(setupId.id),
        password = encryptor.encrypt(webSetup.password),
        apiKey = encryptor.encrypt(webSetup.apiKey)
      ).asJson.spaces2
    } else {
      // only file setup is left possible
      val fileSetup: FileSetup = setup.asInstanceOf[FileSetup]
      fileSetup.copy(id = Some(setupId.id))
        .asJson.spaces2
    }
    configStorage.saveConnectorSetup(userName, setupId, jsonString)
  }

  def getSetup(userName: String, setupId: SetupId): ConnectorSetup = {
    val string = configStorage.loadConnectorSetupAsString(userName, setupId)
    val json = parseSetupStringToJson(string, userName).get

    val maybeParsedConfig = convertJsonSetupsToConnectorSetups(Seq(json)).head
    if (maybeParsedConfig.isDefined) {
      maybeParsedConfig.get
    } else {
      throw new RuntimeException(s"Cannot parse config $json")
    }
  }

  def getAllConnectorSetups(userLoginName: String): Seq[ConnectorSetup] = {
    val setups: Seq[Json] = configStorage.getAllConnectorSetupsAsStrings(userLoginName).flatMap { setupString =>
      parseSetupStringToJson(setupString, userLoginName)
    }
    convertJsonSetupsToConnectorSetups(setups).flatten
  }

  def getAllConnectorSetups(userLoginName: String, connectorId: String): Seq[ConnectorSetup] = {
    val allForUser = getAllConnectorSetups(userLoginName)
    allForUser.filter(setup =>
      setup.connectorId == connectorId)
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
    val mappingsStr = JsonFactory.toString(mappings)
    configStorage.saveConfig(normalizedSyncConfig.getOwnerName, normalizedSyncConfig.identity, label,
      config1.getConnectorTypeId, SetupId(config1.getConnectorSetup.id.get), config1.getConfigString,
      config2.getConnectorTypeId, SetupId(config2.getConnectorSetup.id.get), config2.getConfigString,
      mappingsStr)
  }

  def deleteConfig(configId: ConfigId): Unit = {
    configStorage.deleteConfig(configId)
  }

  def deleteSetup(userName: String, id: SetupId): Unit = {
    configStorage.deleteSetup(userName, id)
  }

  /**
    * @param userLoginName name of the new config owner.
    * @param configId      unique identifier for config to clone
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
          connector2.connectorTypeId, connector2.connectorSavedSetupId, connector2.serializedConfig,
          config.getMappingsString)
      case None => throw new StorageException(s"Cannot find config with id $configId to clone")
    }
  }

  def getConfigIdsUsingThisSetup(userName: String, id: SetupId): Seq[ConfigId] = {
    configStorage.getUserConfigs(userName).filter(c => c.getConnector1.connectorSavedSetupId == id
      || c.getConnector2.connectorSavedSetupId == id).map(c => ConfigId(userName, c.getId))
  }
}
