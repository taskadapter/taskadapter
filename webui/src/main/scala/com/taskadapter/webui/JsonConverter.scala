package com.taskadapter.webui

import java.io.File

import com.google.common.base.Charsets
import com.google.common.io.Files
import net.liftweb.json.{parse, _}

import scala.collection.mutable.Seq

object JsonConverter {
  implicit val formats = DefaultFormats

  def convertFilesToObject[T](files: Array[File])(implicit man: Manifest[T]): Seq[T] = {
    files.map(Files.toString(_, Charsets.UTF_8)).flatMap { string =>
      val jValue = parse(string)
      val saveResult = jValue.extract[T]
      Some(saveResult)
    }
  }
}
