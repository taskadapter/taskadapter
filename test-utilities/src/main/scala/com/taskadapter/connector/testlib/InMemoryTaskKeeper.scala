package com.taskadapter.connector.testlib

import com.taskadapter.core.TaskKeeper

class InMemoryTaskKeeper extends TaskKeeper {
  val map = scala.collection.mutable.Map[String, Long]()

  override def loadTasks(): Map[String, Long] = map.toMap

  override def keepTask(sourceKey: String, targetId: Long): Unit = {
    map += (sourceKey -> targetId)
  }

  override def store(): Unit = {
    // ignore
  }
}
