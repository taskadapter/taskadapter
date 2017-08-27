package com.taskadapter.webui.results

import java.io.{File, FilenameFilter}

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.taskadapter.connector.common.FileNameGenerator
import com.taskadapter.web.uiapi.ConfigId
import net.liftweb.json.Serialization.writePretty
import net.liftweb.json._
import org.slf4j.LoggerFactory

import scala.collection.mutable._

object ExportResultStorage {
  private val logger = LoggerFactory.getLogger(ExportResultStorage.getClass)
  private val resultsFileFilter = new FilenameFilter {
    override def accept(dir: File, name: String): Boolean = name.startsWith("results_") && name.endsWith(".json")
  }

  implicit val formats = DefaultFormats

  def store(rootDir: File, result: ExportResultFormat, maxNumberOfResultsToKeep: Int): Unit = {
    val resultsFolder = getResultsFolder(rootDir)
    resultsFolder.mkdirs()
    ensureMaxNumberResults(rootDir, maxNumberOfResultsToKeep)
    val file = FileNameGenerator.createSafeAvailableFile(resultsFolder, "results_%d.json", 10000000)
    val jsonString = writePretty(result)
    Files.write(jsonString, file, Charsets.UTF_8)
    logger.debug(s"Saved export result to ${file.getAbsolutePath}")
  }

  def ensureMaxNumberResults(rootDir: File, maxNumberOfResultsToKeep: Int): Unit = {
    val files = getResultsFolder(rootDir)
      .listFiles(resultsFileFilter)
    if (files != null && files.size >= maxNumberOfResultsToKeep) {
      val oldest = files.minBy(f => f.lastModified())
      logger.warn(s"Deleting old result file ${oldest.getAbsolutePath} (because max allowed number of results reached: $maxNumberOfResultsToKeep)")
      oldest.delete()
    }
  }

  def getSaveResults(rootDir: File, configId: ConfigId): Seq[ExportResultFormat] = {
    getSaveResults(rootDir).filter(r => r.configId == configId)
  }

  def getSaveResults(rootDir: File): Seq[ExportResultFormat] = {
    val files = getResultsFolder(rootDir)
      .listFiles(resultsFileFilter)
    if (files == null) return Seq()

    convertFilesToObject(files)
  }

  private def convertFilesToObject(files: Array[File]): Seq[ExportResultFormat] = {
    files.map(Files.toString(_, Charsets.UTF_8)).flatMap { string =>
      val jValue = parse(string)
      val saveResult = jValue.extract[ExportResultFormat]
      Some(saveResult)
    }
  }

  private def getResultsFolder(root: File): File = {
    new File(root, "results")
  }
}
