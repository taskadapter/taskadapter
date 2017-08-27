package com.taskadapter

import java.io.{File, FilenameFilter}

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.taskadapter.connector.common.{ConfigUtils, FileNameGenerator}
import com.taskadapter.connector.definition.SaveResult
import com.taskadapter.web.uiapi.ConfigId
import org.slf4j.LoggerFactory

object SaveResultStorage {
  private val logger = LoggerFactory.getLogger(SaveResultStorage.getClass)
  val resultsFileFilter = new FilenameFilter {
    override def accept(dir: File, name: String): Boolean = name.startsWith("results_") && name.endsWith(".json")
  }

  def store(rootDir: File, result: SaveResult): Unit = {
    logger.debug("Saved export result")
    val file = FileNameGenerator.createSafeAvailableFile(getResultsFolder(rootDir), "results_%d.json")
    val jsonString = ConfigUtils.createDefaultGson.toJson(result)
    file.mkdirs()
    Files.write(jsonString, file, Charsets.UTF_8)
  }

  def getSaveResults(rootDir: File, configId: ConfigId): Seq[SaveResult] = {
    Seq()
  }

  def getSaveResults(rootDir: File): Seq[SaveResult] = {
    val files = getResultsFolder(rootDir)
      .listFiles(resultsFileFilter)
    if (files == null) return Seq()

    files.map(Files.toString(_, Charsets.UTF_8)).flatMap { string =>
      Some(ConfigUtils.createDefaultGson.fromJson(string, classOf[SaveResult]))
    }
  }

  private def getResultsFolder(root: File): File = {
    new File(root, "results")
  }
}
