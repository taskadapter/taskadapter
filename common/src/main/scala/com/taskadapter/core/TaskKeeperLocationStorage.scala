package com.taskadapter.core

import java.io.File

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.taskadapter.connector.definition.TaskId
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

import scala.util.Random

case class TaskKeeperLocation(location1: String, location2: String, cacheFileLocation: String)

case class PreviouslyCreatedTasksCache(location1: String, location2: String, items: Seq[(TaskId, TaskId)])

class TaskKeeperLocationStorage(rootFolder: File) {
  val cacheFolder = new File(rootFolder, "cache")
  val fileName = "cache_file_links.json"

  /**
    * Store elements in the cache on disk. New items are added to existing ones, skipping duplicates.
    * If file does not exist yet, it will be created.
    */
  def store(location1: String, location2: String, items: Seq[(TaskId, TaskId)]): Unit = {
    val file = getOrCreateFileLocation(location1, location2)
    val existingCache = loadCache(location1, location2)
    val allItems = (existingCache.items ++ items).distinct
    val newCache = PreviouslyCreatedTasksCache(location1, location2, allItems)
    val json = newCache.asJson
    val formattedJson = json.spaces2
    Files.write(formattedJson, file, Charsets.UTF_8)
  }

  def loadCache(location1: String, location2: String): PreviouslyCreatedTasksCache = {
    val file = getOrCreateFileLocation(location1, location2)
    val fileBody = Files.toString(file, Charsets.UTF_8)
    if (fileBody.isEmpty) {
      PreviouslyCreatedTasksCache("", "", Seq())
    } else {
      val cache = decode[PreviouslyCreatedTasksCache](fileBody)
      cache match {
        case Right(c) => c
        case Left(e) =>
          throw new RuntimeException(s"cannot parse tasks map from file $file: $e")
      }
    }
  }

  def loadTasks(location1: String, location2: String): PreviouslyCreatedTasksResolver = {
    new PreviouslyCreatedTasksResolver(loadCache(location1, location2))
  }

  private def saveCacheLocation(location1: String, location2: String, newFileName: String): Unit = {
    val previousEntries = loadCache()
    val newEntries = previousEntries ++ List(TaskKeeperLocation(location1, location2, newFileName))

    val jsonString = newEntries.asJson.spaces2
    Files.write(jsonString, new File(cacheFolder, fileName), Charsets.UTF_8)
  }

  private def loadCache(): Seq[TaskKeeperLocation] = {
    val file = new File(cacheFolder, fileName)
    if (file.exists()) {
      val jsonString = Files.toString(file, Charsets.UTF_8)
      decode[Seq[TaskKeeperLocation]](jsonString) match {
        case Left(e) => throw new RuntimeException(s"cannot load file with cache definitions: $e")
        case Right(list) => list
      }
    } else {
      List()
    }
  }

  private def getOrCreateFileLocation(location1: String, location2: String): File = {
    val linksToCaches = loadCache()
    val filePath = findCacheLocation(linksToCaches, location1, location2)
    filePath match {
      case None =>
        val newFileName = Math.abs(Random.nextInt()) + ".json"
        val newFile = new File(cacheFolder, newFileName)
        cacheFolder.mkdirs()
        newFile.createNewFile()

        saveCacheLocation(location1, location2, newFileName)
        newFile.getAbsoluteFile
      case Some(p) =>
        new File(cacheFolder, p).getAbsoluteFile
    }
  }

  private def findCacheLocation(caches: Seq[TaskKeeperLocation], location1: String, location2: String): Option[String] = {
    caches.find(e =>
      // either direction is fine
      (e.location1 == location1 && e.location2 == location2)
        ||
        (e.location2 == location1 && e.location1 == location2)
    ).map(_.cacheFileLocation)
  }

}