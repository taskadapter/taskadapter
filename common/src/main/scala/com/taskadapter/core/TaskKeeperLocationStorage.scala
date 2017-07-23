package com.taskadapter.core

import java.io.File

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.taskadapter.connector.definition.TaskId
import io.circe.{Decoder, Encoder}
import io.circe.generic.auto._
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.parser._
import io.circe.syntax._

import scala.util.Random

case class TaskKeeperLocation(location1: String, location2: String, cacheFileLocation: String)

object TaskKeeperLocationStorage {
/*
  implicit val fooDecoder: Decoder[TaskId] = deriveDecoder[TaskId]
  implicit val fooEncoder: Encoder[TaskId] = deriveEncoder[TaskId]
  implicit val mapDecoder: Decoder[Map[TaskId,TaskId]] = deriveDecoder[Map[TaskId,TaskId]]
  implicit val mapEncoder: Encoder[Map[TaskId,TaskId]] = deriveEncoder[Map[TaskId,TaskId]]
*/

  val fileName = "cache_file_links.json"

  def store(configRootFolder: File, location1: String, location2: String, items: Seq[(TaskId, TaskId)]): Unit = {
    val file = getOrCreateFileLocation(configRootFolder, location1, location2)
    val jsonString = items.asJson.spaces2
    Files.write(jsonString, file, Charsets.UTF_8)
  }

  def loadTasks(configRootFolder: File, location1: String, location2: String): PreviouslyCreatedTasksResolver = {
    val file = getOrCreateFileLocation(configRootFolder, location1, location2)
    val fileBody = Files.toString(file, Charsets.UTF_8)
    if (fileBody.isEmpty) {
      new PreviouslyCreatedTasksResolver
    } else {
      val map = decode[Seq[(TaskId, TaskId)]](fileBody)
      map match {
        case Left(e) =>
          throw new RuntimeException(s"cannot parse tasks map from file $file: $e")
        case Right(m) => new PreviouslyCreatedTasksResolver(m)
      }
    }
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