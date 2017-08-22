package com.taskadapter.core

import java.io.File
import java.util

import com.taskadapter.connector.NewConnector
import com.taskadapter.connector.common.TreeUtils
import com.taskadapter.connector.definition.exceptions.ConnectorException
import com.taskadapter.connector.definition.{DropInConnector, ProgressMonitor}
import com.taskadapter.model.GTask

import scala.collection.JavaConverters._

/**
  * Task loader. Implements strategies to load a list of tasks.
  */
object TaskLoader {
  @throws[ConnectorException]
  def loadTasks(maxTasksNumber: Int, connectorFrom: NewConnector, sourceName: String, monitor: ProgressMonitor): util.List[GTask] = {

    val flatTasksList = connectorFrom.loadData().asScala
    val upToNflatTasksList = getUpToNTasks(maxTasksNumber, flatTasksList)
    TreeUtils.buildTreeFromFlatList(upToNflatTasksList.asJava)
  }

  @throws[ConnectorException]
  def loadDropInTasks(maxTasksNumber: Int, connectorFrom: DropInConnector, dropFile: File, monitor: ProgressMonitor): util.List[GTask] = {
    monitor.beginTask("Loading data from uploaded file", 100)
    val flatTasksList = connectorFrom.loadDropInData(dropFile, monitor).asScala

    val tasks = TreeUtils.buildTreeFromFlatList(getUpToNTasks(maxTasksNumber,
      flatTasksList.sortBy(_.getId))
      .asJava)
    monitor.done()
    tasks
  }

  private def getUpToNTasks(maxTasksNumber: Int, flatTasksList: Seq[GTask]) = {
    val tasksToLeave = Math.min(maxTasksNumber, flatTasksList.size)
    flatTasksList.slice(0, tasksToLeave)
  }
}