package com.taskadapter.core

class TaskKeeper {
  val map = scala.collection.mutable.Map[String, Long]()

  def keepTask(sourceKey: String, targetId: Long): Unit = {
    map += (sourceKey -> targetId)
  }
}
