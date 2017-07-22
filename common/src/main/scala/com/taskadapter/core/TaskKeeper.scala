package com.taskadapter.core

import java.io.File

import com.google.common.base.Charsets
import com.google.common.io.Files
import io.circe.parser._
import io.circe.syntax._

trait TaskKeeper {
  def keepTask(sourceKey: String, targetId: Long): Unit

  def loadTasks(): Map[String, Long]
  def store(): Unit
}

class FileTaskKeeper(rootFolder: File) extends TaskKeeper {
  val map = scala.collection.mutable.Map[String, Long]()

  override def store() : Unit = {
    val stringMap: Map[String, Long] = map.toMap
    val jsonString = stringMap.asJson.noSpaces
    val newFile = new File(rootFolder, "createdtasks.json")
    Files.write(jsonString, newFile, Charsets.UTF_8)
  }

  override def loadTasks(): Map[String, Long] = {
    val file = new File(rootFolder, "createdtasks.json")
    if (file.exists()) {
      val fileBody = Files.toString(file, Charsets.UTF_8)
      val map = decode[Map[String, Long]](fileBody)
      map match {
        case Left(e) => throw new RuntimeException(s"cannot parse tasks map from file $file: $e")
        case Right(m) => m
      }
    } else {
      Map()
    }
  }

  override def keepTask(sourceKey: String, targetKey: Long): Unit = {
    map += (sourceKey -> targetKey)
  }
}
