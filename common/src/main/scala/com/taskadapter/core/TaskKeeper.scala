package com.taskadapter.core

import java.io.File

import com.google.common.base.Charsets
import com.google.common.io.Files
import io.circe.parser._
import io.circe.syntax._
import scala.collection.JavaConverters._

class TaskKeeper(rootFolder: File) {

  /**
    * Save a map with tasks info: originalId->newId
    */
  def keepTasks(tasksMap: java.util.Map[Integer, String]): Unit = {
    val stringMap : Map[String,String] = tasksMap.asScala.map(e => e._1.toString -> e._2).toMap
    val jsonString = stringMap.asJson.noSpaces
    val newFile = new File(rootFolder, "createdtasks.json")
    Files.write(jsonString, newFile, Charsets.UTF_8)
  }

  def loadTasks(): Map[String, String] = {
    val file = new File(rootFolder, "createdtasks.json")
    val fileBody = Files.toString(file, Charsets.UTF_8)
    val map = decode[Map[String, String]](fileBody)
    map match {
      case Left(e) => throw new RuntimeException(s"cannot parse tasks map from file $file: $e")
      case Right(m) => m
    }
  }

}
