package com.taskadapter.connector.testlib

import java.util
import java.util.{Calendar, Date}

import com.taskadapter.connector.common.ProgressMonitorUtils
import com.taskadapter.connector.definition.exceptions.ConnectorException
import com.taskadapter.connector.definition.{SaveResult, TaskId}
import com.taskadapter.connector.{FieldRow, NewConnector}
import com.taskadapter.core.{PreviouslyCreatedTasksCache, PreviouslyCreatedTasksResolver, TaskLoader}
import com.taskadapter.model.{DueDate, Field, GTask, StartDate}

import scala.collection.JavaConverters._

object TestUtils {

  def findTaskInList(list: util.List[GTask], createdTaskId: TaskId): Option[GTask] = {
    list.asScala.find(_.getIdentity == createdTaskId)
  }

  def findTaskByKey(list: util.List[GTask], key: String): GTask = {
    list.asScala.find(_.getKey == key).orNull
  }

  def findTaskByFieldName(list: Seq[GTask], field: Field[_], value: String): GTask = {
    list.find(_.getValue(field) == value).orNull
  }

  @throws[ConnectorException]
  def saveAndLoadAll(connector: NewConnector, task: GTask, rows: List[FieldRow[_]]): List[GTask] = {
    connector.saveData(PreviouslyCreatedTasksResolver.empty, List(task).asJava, ProgressMonitorUtils.DUMMY_MONITOR, rows)
    connector.loadData().asScala.sortBy(_.getId).toList
  }

  @throws[ConnectorException]
  def saveAndLoadList(connector: NewConnector, tasks: Seq[GTask], rows: Seq[FieldRow[_]]): List[GTask] = {
    connector.saveData(PreviouslyCreatedTasksResolver.empty, tasks.asJava, ProgressMonitorUtils.DUMMY_MONITOR, rows)
    connector.loadData().asScala.sortBy(_.getId).toList
  }

  /**
    * Uses a NEW instance of PreviouslyCreatedTasksResolver (empty) for each call, so this won't work for some tests
    * that require updates.
    */
  @throws[ConnectorException]
  def saveAndLoad(connector: NewConnector, task: GTask, rows: Seq[FieldRow[_]]): GTask = {
    val result = connector.saveData(PreviouslyCreatedTasksResolver.empty, util.Arrays.asList(task), ProgressMonitorUtils.DUMMY_MONITOR, rows)
    val remoteKeys = result.getRemoteKeys
    val remoteKey = remoteKeys.iterator.next
    connector.loadTaskByKey(remoteKey, rows)
  }

  /**
    * Load task that was previously created and its result is saved in [[SaveResult]]
    */
  @throws[ConnectorException]
  def loadCreatedTask(connector: NewConnector, rows: Seq[FieldRow[_]], result: SaveResult): GTask = {
    val remoteKey = result.getRemoteKeys.head
    connector.loadTaskByKey(remoteKey, rows)
  }

  /**
    * @return the new task Key
    */

  def saveAndLoadViaSummary(connector: NewConnector, task: GTask, rows: List[FieldRow[_]], fieldToSearch:Field[_]): GTask = {
    val loadedTasks = saveAndLoadAll(connector, task, rows)
    findTaskByFieldName(loadedTasks, fieldToSearch, task.getValue(fieldToSearch).toString)
  }


  @throws[ConnectorException]
  def save(connector: NewConnector, task: GTask, rows: List[FieldRow[_]]): TaskId = {
    val result = connector.saveData(PreviouslyCreatedTasksResolver.empty, List(task).asJava, ProgressMonitorUtils.DUMMY_MONITOR, rows)
    val remoteKeys = result.getRemoteKeys
    remoteKeys.iterator.next
  }

  /**
    * @param rows source-target field rows
    */
  def loadAndSave(sourceConnector: NewConnector, targetConnector: NewConnector,
                  rows: Seq[FieldRow[_]]): GTask = {
    val loadedTask = TaskLoader.loadTasks(1, sourceConnector, "sourceName", ProgressMonitorUtils.DUMMY_MONITOR).asScala.toList.head
    val result = TestUtils.saveAndLoadList(targetConnector, Seq(loadedTask), rows).head
    result
  }

  def loadAndSaveList(sourceConnector: NewConnector, targetConnector: NewConnector,
                  rows: Seq[FieldRow[_]]): List[GTask] = {
    val loadedTasks = TaskLoader.loadTasks(1000, sourceConnector, "sourceName", ProgressMonitorUtils.DUMMY_MONITOR).asScala.toList
    val result = TestUtils.saveAndLoadList(targetConnector, loadedTasks, rows)
    result
  }

  def setTaskStartYearAgo(task: GTask): Calendar = {
    val yearAgo = DateUtils.getCalendarRoundedToDay
    yearAgo.add(Calendar.YEAR, -1)
    task.setValue(StartDate, yearAgo.getTime)
    yearAgo
  }

  def yearAgo: Date = {
    val yearAgo = DateUtils.getCalendarRoundedToDay
    yearAgo.add(Calendar.YEAR, -1)
    yearAgo.getTime
  }

  def nextYear: Date = {
    val cal = DateUtils.getCalendarRoundedToDay
    cal.add(Calendar.YEAR, 1)
    cal.getTime
  }
}
