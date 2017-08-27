package com.taskadapter

import java.io.{File, FilenameFilter}

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.taskadapter.connector.common.FileNameGenerator
import com.taskadapter.connector.definition.SaveResult
import com.taskadapter.web.uiapi.ConfigId
import net.liftweb.json.Serialization.writePretty
import net.liftweb.json._
import org.slf4j.LoggerFactory

import scala.collection.mutable._

object SaveResultStorage {
  private val logger = LoggerFactory.getLogger(SaveResultStorage.getClass)
  val resultsFileFilter = new FilenameFilter {
    override def accept(dir: File, name: String): Boolean = name.startsWith("results_") && name.endsWith(".json")
  }

  implicit val formats = DefaultFormats

  def store(rootDir: File, result: SaveResult): Unit = {
    logger.debug("Saved export result")
    val resultsFolder = getResultsFolder(rootDir)
    resultsFolder.mkdirs()
    val file = FileNameGenerator.createSafeAvailableFile(resultsFolder, "results_%d.json")
    val jsonString = writePretty(result)
    Files.write(jsonString, file, Charsets.UTF_8)
  }

  def getSaveResults(rootDir: File, configId: ConfigId): Seq[SaveResult] = {
    getSaveResults(rootDir) //.filter(r => r.configId == configId)
  }

  def getSaveResults(rootDir: File): Seq[SaveResult] = {
    val files = getResultsFolder(rootDir)
      .listFiles(resultsFileFilter)
    if (files == null) return Seq()

    files.map(Files.toString(_, Charsets.UTF_8)).flatMap { string =>
      val jValue = parse(string)
      val saveResult = jValue.extract[SaveResult]
      Some(saveResult)
    }
  }

  private def getResultsFolder(root: File): File = {
    new File(root, "results")
  }
}
