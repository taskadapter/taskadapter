package com.taskadapter.webui

import java.io.File

import org.apache.log4j.RollingFileAppender

object LogFinder {
  def getLogFileLocation(): String = {
    val logger = org.apache.log4j.Logger.getRootLogger
    val allAppenders = logger.getAllAppenders
    while (allAppenders.hasMoreElements) {
      val e = allAppenders.nextElement
      if (e.isInstanceOf[RollingFileAppender]) {
        // found it
        return new File(((e.asInstanceOf[RollingFileAppender]).getFile)).getAbsolutePath
      }
    }
    ""
  }
}