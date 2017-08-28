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

class ExportResultStorage(rootDir: File, maxNumberOfResultsToKeep: Int) {
  private val logger = LoggerFactory.getLogger(classOf[ExportResultStorage])
  private val resultsFileFilter = new FilenameFilter {
    override def accept(dir: File, name: String): Boolean = name.startsWith("results_") && name.endsWith(".json")
  }

  implicit val formats = DefaultFormats

  val resultsFolder = new File(rootDir, "results")

  def store(result: ExportResultFormat ): Unit = {
    resultsFolder.mkdirs()
    ensureMaxNumberResults(maxNumberOfResultsToKeep)
    val file = FileNameGenerator.createSafeAvailableFile(resultsFolder, "results_%d.json", 10000000)
    val jsonString = writePretty(result)
    Files.write(jsonString, file, Charsets.UTF_8)
    logger.debug(s"Saved export result to ${file.getAbsolutePath}")
  }

  def getSaveResults(configId: ConfigId): Seq[ExportResultFormat] = {
    getSaveResults().filter(r => r.configId == configId)
  }

  def getSaveResults(): Seq[ExportResultFormat] = {
    val files = resultsFolder
      .listFiles(resultsFileFilter)
    if (files == null) return Seq()

    convertFilesToObject(files)
  }

  private def ensureMaxNumberResults(maxNumberOfResultsToKeep: Int): Unit = {
    val files = resultsFolder
      .listFiles(resultsFileFilter)
    if (files != null && files.size >= maxNumberOfResultsToKeep) {
      val oldest = files.minBy(f => f.lastModified())
      logger.warn(s"Deleting old result file ${oldest.getAbsolutePath} (because max allowed number of results reached: $maxNumberOfResultsToKeep)")
      oldest.delete()
    }
  }

  private def convertFilesToObject(files: Array[File]): Seq[ExportResultFormat] = {
    files.map(Files.toString(_, Charsets.UTF_8)).flatMap { string =>
      val jValue = parse(string)
      val saveResult = jValue.extract[ExportResultFormat]
      Some(saveResult)
    }
  }
}
