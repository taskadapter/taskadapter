package com.taskadapter.webui.results

import java.io.File

import com.taskadapter.web.uiapi.ConfigId
import com.taskadapter.webui.Storage
import org.slf4j.LoggerFactory

import scala.collection.mutable._

class ExportResultStorage(rootDir: File, maxNumberOfResultsToKeep: Int) {
  private val logger = LoggerFactory.getLogger(classOf[ExportResultStorage])
  val dataFolder = new File(rootDir, "results")
  val storage = new Storage(dataFolder, "results", "json")

  def store(result: ExportResultFormat ): Unit = {
    ensureMaxNumberResults(maxNumberOfResultsToKeep)
    storage.store(result)
  }

  def getSaveResults(configId: ConfigId): Seq[ExportResultFormat] = {
    getSaveResults().filter(r => r.configId == configId)
  }

  def getSaveResults(): Seq[ExportResultFormat] = {
    storage.getItems[ExportResultFormat]()
  }

  private def ensureMaxNumberResults(maxNumberOfResultsToKeep: Int): Unit = {
    val files = dataFolder
      .listFiles()
    if (files != null && files.size >= maxNumberOfResultsToKeep) {
      val oldest = files.minBy(f => f.lastModified())
      logger.warn(s"Deleting old result file ${oldest.getAbsolutePath} (because max allowed number of results reached: $maxNumberOfResultsToKeep)")
      oldest.delete()
    }
  }
}
