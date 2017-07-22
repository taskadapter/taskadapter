package com.taskadapter.core

import java.io.File

import com.google.common.base.Charsets
import com.google.common.io.Files
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

import scala.util.Random

case class TaskKeeperLocation(location1: String, location2: String, cacheFileLocation: String)

object TaskKeeperLocationStorage {

  val fileName = "cache_file_links.json"

  def getFileLocation(configRootFolder: File, location1: String, location2: String): Option[String] = {
    val caches = loadCache(configRootFolder)
    findCacheLocation(caches, location1, location2)
  }

  def findCacheLocation(caches: Seq[TaskKeeperLocation], location1: String, location2: String): Option[String] = {
    caches.find(e =>
      // either direction is fine
      (e.location1 == location1 && e.location2 == location2)
        ||
        (e.location2 == location1 && e.location1 == location2)
    ).map(_.cacheFileLocation)
  }

  def saveCacheLocation(configRootFolder: File, location1: String, location2: String): Unit = {
    val cacheFileName = Random.nextString(5)

    val previousEntries = loadCache(configRootFolder)
    val newEntries = previousEntries ++ List(TaskKeeperLocation(location1, location2, cacheFileName))

    val jsonString = newEntries.asJson.spaces2
    Files.write(jsonString, new File(configRootFolder, fileName), Charsets.UTF_8)
  }

  def loadCache(configRootFolder: File): Seq[TaskKeeperLocation] = {
    val jsonString = Files.toString(new File(configRootFolder, fileName), Charsets.UTF_8)
    decode[Seq[TaskKeeperLocation]](jsonString) match {
      case Left(e) => throw new RuntimeException(s"cannot load file with cache definitions: $e")
      case Right(list) => list
    }
  }
}