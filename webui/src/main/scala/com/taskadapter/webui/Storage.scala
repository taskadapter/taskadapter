package com.taskadapter.webui

import java.io.{File, FilenameFilter}

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.taskadapter.connector.common.FileNameGenerator
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.writePretty
import org.slf4j.LoggerFactory

import scala.collection.mutable.Seq

class Storage(storageFolder: File, fileNamePrefix: String, fileNameExtention: String) {
  private val logger = LoggerFactory.getLogger(classOf[Storage])
  private val resultsFileFilter = new FilenameFilter {
    override def accept(dir: File, name: String): Boolean = name.startsWith(fileNamePrefix) && name.endsWith(fileNameExtention)
  }

  implicit val formats = DefaultFormats

  def store[T](result: T): Unit = {
    storageFolder.mkdirs()
    val file = FileNameGenerator.createSafeAvailableFile(storageFolder, s"${fileNamePrefix}_%d.$fileNameExtention", 10000000)
    val jsonString = writePretty(result)
    Files.write(jsonString, file, Charsets.UTF_8)
    logger.debug(s"Saved $result to ${file.getAbsolutePath}")
  }

  def getItems[T]()(implicit man: Manifest[T]): Seq[T] = {
    val files = storageFolder
      .listFiles(resultsFileFilter)
    if (files == null) return Seq()

    JsonConverter.convertFilesToObject[T](files)
  }
}
