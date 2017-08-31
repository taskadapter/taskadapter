package com.taskadapter.webui

import java.io.File

import com.google.common.base.Charsets
import com.google.common.io.Files
import net.liftweb.json.{parse, _}

import scala.collection.mutable.Seq

object JsonConverter {
  implicit val formats = DefaultFormats

  def convertFilesToObject[T](files: Array[File])(implicit man: Manifest[T]): Seq[T] = {
    files.map(convertFileToObject[T])
  }

  def convertFileToObject[T](file: File)(implicit man: Manifest[T]): T = {
    val jValue = parse(Files.toString(file, Charsets.UTF_8))
    val saveResult = jValue.extract[T]
    saveResult
  }
}
