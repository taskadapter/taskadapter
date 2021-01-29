package com.taskadapter.connector.testlib

import java.util
import java.util.{Calendar, Date}

import com.taskadapter.connector.common.ProgressMonitorUtils
import com.taskadapter.connector.definition.exceptions.ConnectorException
import com.taskadapter.connector.{FieldRow, NewConnector}
import com.taskadapter.core.PreviouslyCreatedTasksResolver
import com.taskadapter.model.{Field, GTask, StartDate}

import scala.collection.JavaConverters._

object TestUtils {

  def findTaskByFieldName(list: Seq[GTask], field: Field[_], value: String): GTask = {
    list.find(_.getValue(field) == value).orNull
  }

  @throws[ConnectorException]
  def saveAndLoadAll(connector: NewConnector, task: GTask, rows: util.List[FieldRow[_]]): List[GTask] = {
    connector.saveData(PreviouslyCreatedTasksResolver.empty, List(task).asJava, ProgressMonitorUtils.DUMMY_MONITOR, rows)
    connector.loadData().asScala.sortBy(_.getId).toList
  }

  /**
    * @return the new task Key
    */

  def saveAndLoadViaSummary(connector: NewConnector, task: GTask, rows: util.List[FieldRow[_]], fieldToSearch:Field[_]): GTask = {
    val loadedTasks = saveAndLoadAll(connector, task, rows)
    findTaskByFieldName(loadedTasks, fieldToSearch, task.getValue(fieldToSearch).toString)
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
