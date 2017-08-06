package com.taskadapter.config

import java.io.{File, FilenameFilter, IOException}
import java.util

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.taskadapter.web.uiapi.ConfigId
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

object ConfigStorage {
  private val configFileExtension = "ta_conf"
  val NUMBER_SEPARATOR = "_"
  val CONFIG_FILE_FILTER = new FilenameFilter {
    override def accept(dir: File, name: String): Boolean = name.endsWith(configFileExtension)
  }
  private val setupFileExtension = "json"
  val setupFileFilter = new FilenameFilter {
    override def accept(dir: File, name: String): Boolean = name.endsWith(setupFileExtension)
  }
}

class ConfigStorage(val rootDir: File) {
  private val logger = LoggerFactory.getLogger(classOf[ConfigStorage])

  def getConfig(configId: ConfigId): Option[StoredExportConfig] = {
    getConfigsInFolder(getUserConfigsFolder(configId.ownerName)).find(_.getId == configId.id)
  }

  def getUserConfigs(userLoginName: String): Seq[StoredExportConfig] = getConfigsInFolder(getUserConfigsFolder(userLoginName))

  def getUserFolder(userLoginName: String): File = {
    new File(rootDir, userLoginName)
  }

  private def getUserConfigsFolder(userLoginName: String) = {
    new File(getUserFolder(userLoginName), "configs")
  }


  private def getConfigsInFolder(folder: File): Seq[StoredExportConfig] = {
    val configs = folder.listFiles(ConfigStorage.CONFIG_FILE_FILTER)
    if (configs == null) return Seq()
    configs.toSeq.flatMap { file =>
      try {
        val fileBody = Files.toString(file, Charsets.UTF_8)
        Some(NewConfigParser.parse(file.getAbsolutePath, fileBody))
      } catch {
        case e: Exception =>
          logger.error("Error loading file " + file.getAbsolutePath + ": " + e.getMessage, e)
          None
      }
    }
  }

  @throws[StorageException]
  def saveConfig(userLoginName: String, configId: String, configName: String,
                 connector1Id: String, connector1Data: String,
                 connector2Id: String, connector2Data: String, mappings: String): Unit = {
    logger.info(s"Saving config for user $userLoginName")
    val fileContents = NewConfigParser.toFileContent(configName, connector1Id, connector1Data, connector2Id, connector2Data, mappings)
    try {
      val folder = getUserConfigsFolder(userLoginName)
      folder.mkdirs
      Files.write(fileContents, new File(configId), Charsets.UTF_8)
    } catch {
      case e: IOException =>
        throw new StorageException(e)
    }
  }

  /**
    * @return unique id for the new config to find it in the storage
    */
  @throws[StorageException]
  def createNewConfig(userLoginName: String, configName: String, connector1Id: String, connector1Data: String,
                      connector2Id: String, connector2Data: String, mappings: String): ConfigId = {
    val fileContents = NewConfigParser.toFileContent(configName, connector1Id, connector1Data, connector2Id, connector2Data, mappings)
    try {
      val folder = getUserConfigsFolder(userLoginName)
      folder.mkdirs
      val newConfigFile = findUnusedConfigFile(folder, connector1Id, connector2Id)
      Files.write(fileContents, newConfigFile, Charsets.UTF_8)
      ConfigId(userLoginName, newConfigFile.getAbsolutePath)
    } catch {
      case e: IOException =>
        throw new StorageException(e)
    }
  }

  @throws[StorageException]
  def saveConnectorSetup(userLoginName: String, setupLabel: String, connectorSetup: String): Unit = {
    logger.info(s"Saving connector setup for user $userLoginName. label: $setupLabel")
    try {
      val folder = getUserFolder(userLoginName)
      folder.mkdirs
      val newConfigFile = new File(folder, setupLabel)
      Files.write(connectorSetup, newConfigFile, Charsets.UTF_8)
    } catch {
      case e: IOException =>
        throw new StorageException(e)
    }
  }

  @throws[StorageException]
  def loadConnectorSetupAsString(userName: String, setupLabel: String): String = try {
    val folder = getUserFolder(userName)
    val file = new File(folder, setupLabel)
    Files.toString(file, Charsets.UTF_8)
  } catch {
    case e: IOException =>
      throw new StorageException(e)
  }

  def getAllConnectorSetupsAsStrings(userLoginName: String, connectorId: String): util.List[String] = {
    val folder = getUserFolder(userLoginName)
    val setupFiles = folder.listFiles(ConfigStorage.setupFileFilter)
    if (setupFiles == null) return List().asJava

    setupFiles.map(Files.toString(_, Charsets.UTF_8)).toList.asJava
  }

  private def findUnusedConfigFile(userFolder: File, type1: String, type2: String): File = {
    val namePrefix = createFileNamePrefix(type1, type2)
    var fileOrdinal = 1
    var file: File = null
    do {
      file = new File(userFolder, namePrefix + fileOrdinal + "." + ConfigStorage.configFileExtension)
      fileOrdinal += 1
    } while ( {
      file.exists
    })
    file
  }

  private def createFileNamePrefix(type1: String, type2: String): String = {
    var fileName = type1 + "_" + type2 + ConfigStorage.NUMBER_SEPARATOR
    fileName = fileName.replaceAll(" ", "-")
    fileName
  }

  def delete(configId: ConfigId): Unit = {
    new File(configId.id).delete
  }

}