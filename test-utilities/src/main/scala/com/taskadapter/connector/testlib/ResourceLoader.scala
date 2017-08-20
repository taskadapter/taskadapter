package com.taskadapter.connector.testlib

object ResourceLoader {

  def getAbsolutePathForResource(name: String) = {
    val url = ResourceLoader.getClass.getClassLoader.getResource(name)
    try
      url.toURI.getPath
    catch {
      case e: Exception =>
        throw new RuntimeException(e)
    }
  }
}
