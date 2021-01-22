package com.taskadapter.config

import java.io.{File, FilenameFilter, IOException}

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.taskadapter.web.uiapi.{ConfigId, SetupId}
import org.slf4j.LoggerFactory

object ConfigStorage {
  /**
    * file name extension for legacy configs
    *
    * legacy configs do not have "ta.id" field and thus have no numeric id in them. they used full file name
    * as "id" until November 2020. Yay, 2020 is almost over now! I hope you all survived it.
    */
  private val legacyConfigFileExtension = ".ta_conf"
  private val setupFileExtension = "json"

  val CONFIG_FILE_FILTER = new FilenameFilter {
    override def accept(dir: File, name: String): Boolean = name.endsWith(ConfigStorageJava.configFileExtension)
  }

  val LEGACY_CONFIG_FILE_FILTER = new FilenameFilter {
    override def accept(dir: File, name: String): Boolean = name.endsWith(legacyConfigFileExtension)
  }

  val setupFileFilter = new FilenameFilter {
    override def accept(dir: File, name: String): Boolean = name.endsWith(setupFileExtension)
  }
}

class ConfigStorage(val rootDir: File) {
  private val logger = LoggerFactory.getLogger(classOf[ConfigStorage])

  def getConfig(configId: ConfigId): Option[StoredExportConfig] = {
    val file = getConfigFile(configId)
    if (file.exists()) {
      val fileBody = Files.toString(file, Charsets.UTF_8)
      Some(NewConfigParser.parse(fileBody))
    } else {
      None
    }
  }

  def getUserFolder(userLoginName: String): File = {
    ConfigStorageJava.getUserFolder(rootDir, userLoginName)
  }

  def getUserConfigs(userLoginName: String): Seq[StoredExportConfig] = {
    val folder = ConfigStorageJava.getUserConfigsFolder(rootDir, userLoginName)

    val configFiles = folder.listFiles(ConfigStorage.CONFIG_FILE_FILTER)
    val configs = getConfigsInFolder(configFiles)

    val configIds = configs.map(c => c.getId)
    val largestIdInNonLegacyConfigs = if (configIds.nonEmpty) {
      configIds.max
    } else {
      0
    }

    val legacyConfigFiles = folder.listFiles(ConfigStorage.LEGACY_CONFIG_FILE_FILTER)
    val legacyConfigs = getLegacyConfigsInFolder(userLoginName, legacyConfigFiles, largestIdInNonLegacyConfigs)

    configs ++ legacyConfigs
  }

  private def getConfigsInFolder(configFiles: Seq[File]): Seq[StoredExportConfig] = {
    if (configFiles == null) return Seq()
    configFiles.flatMap { file =>
      try {
        val fileBody = Files.toString(file, Charsets.UTF_8)
        Some(NewConfigParser.parse(fileBody))
      } catch {
        case e: Exception =>
          logger.error("Error loading file " + file.getAbsolutePath + ": " + e.getMessage, e)
          None
      }
    }
  }

  /**
    * @param largestIdInNonLegacyConfigs is used to name newly converted configs, to ensure their names do not conflict
    */
  private def getLegacyConfigsInFolder(userLoginName: String, configFiles: Seq[File],
                                       largestIdInNonLegacyConfigs: Int): Seq[StoredExportConfig] = {
    var lastUsedId = largestIdInNonLegacyConfigs
    if (configFiles == null) return Seq()
    configFiles.flatMap { file =>
      try {
        val fileBody = Files.toString(file, Charsets.UTF_8)
        val newId = lastUsedId + 1
        lastUsedId = newId
        val newConfig = NewConfigParser.parseLegacyConfig(newId, fileBody)
        saveConfig(userLoginName, newId, newConfig.getName,
          newConfig.getConnector1.connectorTypeId, newConfig.getConnector1.connectorSavedSetupId, newConfig.getConnector1.serializedConfig,
          newConfig.getConnector2.connectorTypeId, newConfig.getConnector2.connectorSavedSetupId, newConfig.getConnector2.serializedConfig,
          newConfig.getMappingsString)
        file.renameTo(new File(file.getAbsoluteFile + ".bak"))
        Some(newConfig)
      } catch {
        case e: Exception =>
          logger.error("Error loading legacy config file " + file.getAbsolutePath + ": " + e.getMessage, e)
          None
      }
    }
  }

  // TODO TA3 unify saveConfig() and createNewConfig()

  @throws[StorageException]
  def saveConfig(userLoginName: String, configId: Int, configName: String,
                 connector1Id: String, connector1SavedSetupId: SetupId, connector1Data: String,
                 connector2Id: String, connector2SavedSetupId: SetupId, connector2Data: String, mappings: String): Unit = {
    logger.info(s"Saving config " + configId + " for user $userLoginName")
    val fileContents = NewConfigParser.toFileContent(configId, configName, connector1Id, connector1SavedSetupId, connector1Data,
      connector2Id, connector2SavedSetupId, connector2Data, mappings)
    try {
      val folder = ConfigStorageJava.getUserConfigsFolder(rootDir, userLoginName)
      folder.mkdirs
      val newConfigFile = getConfigFile(ConfigId(userLoginName, configId))
      Files.write(fileContents, newConfigFile, Charsets.UTF_8)
    } catch {
      case e: IOException =>
        throw new StorageException(e)
    }
  }

  /**
    * @return unique id for the new config to find it in the storage
    */
  @throws[StorageException]
  def createNewConfig(userLoginName: String, configName: String,
                      connector1Id: String, connector1SavedSetupId: SetupId, connector1Data: String,
                      connector2Id: String, connector2SavedSetupId: SetupId, connector2Data: String,
                      mappings: String): ConfigId = {
    try {
      val folder = ConfigStorageJava.getUserConfigsFolder(rootDir, userLoginName)
      folder.mkdirs
      val newId = ConfigStorageJava.findUnusedConfigId(folder);
      val newConfigFile = getConfigFile(ConfigId(userLoginName, newId))

      val fileContents = NewConfigParser.toFileContent(newId, configName,
        connector1Id, connector1SavedSetupId, connector1Data,
        connector2Id, connector2SavedSetupId, connector2Data,
        mappings)

      Files.write(fileContents, newConfigFile, Charsets.UTF_8)
      ConfigId(userLoginName, newId)
    } catch {
      case e: IOException =>
        throw new StorageException(e)
    }
  }

  @throws[StorageException]
  def saveConnectorSetup(userLoginName: String, setupId: SetupId, connectorSetup: String): Unit = {
    logger.info(s"Saving connector setup for user $userLoginName. id $setupId")
    try {
      val folder = ConfigStorageJava.getUserFolder(rootDir, userLoginName)
      folder.mkdirs
      val newConfigFile = new File(folder, setupId.id)
      Files.write(connectorSetup, newConfigFile, Charsets.UTF_8)
    } catch {
      case e: IOException =>
        throw new StorageException(e)
    }
  }

  @throws[StorageException]
  def loadConnectorSetupAsString(userName: String, setupId: SetupId): String = try {
    val folder = ConfigStorageJava.getUserFolder(rootDir, userName)
    val file = new File(folder, setupId.id)
    Files.toString(file, Charsets.UTF_8)
  } catch {
    case e: IOException =>
      throw new StorageException(e)
  }

  def getAllConnectorSetupsAsStrings(userLoginName: String): Seq[String] = {
    val folder = ConfigStorageJava.getUserFolder(rootDir, userLoginName)
    val setupFiles = folder.listFiles(ConfigStorage.setupFileFilter)
    if (setupFiles == null) return Seq()

    setupFiles.map(Files.toString(_, Charsets.UTF_8)).toSeq
  }

  def deleteConfig(configId: ConfigId): Unit = {
    getConfigFile(configId).delete()
  }

  def deleteSetup(userName: String, id: SetupId): Unit = {
    new File(ConfigStorageJava.getUserFolder(rootDir, userName), id.id).delete
  }

  def getConfigFile(configId: ConfigId): File = {
    new File(ConfigStorageJava.getUserConfigsFolder(rootDir, configId.ownerName),
      ConfigStorageJava.createFileName(configId.id))
  }
}