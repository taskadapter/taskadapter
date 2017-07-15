package com.taskadapter.connector.testlib

import java.util

import com.taskadapter.core.TaskKeeper

import scala.collection.JavaConverters._

class InMemoryTaskKeeper extends TaskKeeper {
  val map = scala.collection.mutable.Map[String, Long]()

  override def keepTasks(tasksMap: util.Map[String, Long]): Unit = {
    map ++= tasksMap.asScala
      .filter(_._1 != null) // skip tasks that did not have original IDs (new tasks not originated from JIRA/Redmine/..)
  }

  override def loadTasks(): Map[String, Long] = map.toMap

  override def keepTask(sourceKey: String, targetId: Long): Unit = {
    map += (sourceKey -> targetId)
  }

  override def store(): Unit = {
    // ignore
  }
}
