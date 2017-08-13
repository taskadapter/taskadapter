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

object TaskKeeperLocationStorage {

  val fileName = "cache_file_links.json"

  def append(configRootFolder: File, location1: String, location2: String, items: Seq[(TaskId, TaskId)]): Unit = {
    val file = getOrCreateFileLocation(configRootFolder, location1, location2)
    val existingCache = loadCache(configRootFolder, location1, location2)
    val allItems = (existingCache.items ++ items).distinct
    val newCache = PreviouslyCreatedTasksCache(location1, location2, allItems)
    val jsonString = newCache.asJson.spaces2
    Files.write(jsonString, file, Charsets.UTF_8)
  }

  def loadCache(configRootFolder: File, location1: String, location2: String): PreviouslyCreatedTasksCache = {
    val file = getOrCreateFileLocation(configRootFolder, location1, location2)
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

  def loadTasks(configRootFolder: File, location1: String, location2: String): PreviouslyCreatedTasksResolver = {
    new PreviouslyCreatedTasksResolver(loadCache(configRootFolder, location1, location2))
  }

  private def saveCacheLocation(configRootFolder: File, location1: String, location2: String, newFileName: String): Unit = {
    val previousEntries = loadCache(configRootFolder)
    val newEntries = previousEntries ++ List(TaskKeeperLocation(location1, location2, newFileName))

    val jsonString = newEntries.asJson.spaces2
    Files.write(jsonString, new File(configRootFolder, fileName), Charsets.UTF_8)
  }

  private def loadCache(configRootFolder: File): Seq[TaskKeeperLocation] = {
    val file = new File(configRootFolder, fileName)
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

  private def getOrCreateFileLocation(configRootFolder: File, location1: String, location2: String): File = {
    val linksToCaches = loadCache(configRootFolder)
    val filePath = findCacheLocation(linksToCaches, location1, location2)
    filePath match {
      case None =>
        val newFileName = Math.abs(Random.nextInt()) + ".json"
        val newFile = new File(configRootFolder, newFileName)
        newFile.createNewFile()

        saveCacheLocation(configRootFolder, location1, location2, newFileName)
        newFile.getAbsoluteFile
      case Some(p) =>
        new File(configRootFolder, p).getAbsoluteFile
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