package com.taskadapter.connector.testlib

import java.util
import java.util.{Calendar, Date}

import com.taskadapter.connector.common.{ConnectorUtils, ProgressMonitorUtils}
import com.taskadapter.connector.definition.SaveResult
import com.taskadapter.connector.definition.exceptions.ConnectorException
import com.taskadapter.connector.{Field, FieldRow, NewConnector}
import com.taskadapter.core.PreviouslyCreatedTasksResolver
import com.taskadapter.model.GTask

import scala.collection.JavaConverters._

object TestUtils {
  /*
     public static List<GTask> generateTasks(int quantity) {
         List<GTask> tasks = new ArrayList<>(quantity);
         for (int i = 0; i < quantity; i++) {
             tasks.add(generateTask());
         }
         return tasks;
     }
 */

  def findTaskInList(list: util.List[GTask], createdTask1Id: Long): GTask = {
    list.asScala.find(_.getId == createdTask1Id).orNull
  }

  def findTaskByKey(list: util.List[GTask], key: String): GTask = {
    list.asScala.find(_.getKey == key).orNull
  }

  /**
    * Use the scala version
    */
  @Deprecated
  def findTaskByFieldName(list: util.List[GTask], fieldName: String, value: String): GTask = {
    findTaskByFieldName(list.asScala.toList, fieldName, value)
  }

  def findTaskByFieldName(list: List[GTask], fieldName: String, value: String): GTask = {
    list.find(_.getValue(fieldName) == value).orNull
  }

  /*
      public static GTask generateTask() {
          GTask t = new GTask();
          long timeInMillis = Calendar.getInstance().getTimeInMillis();
          t.setSummary("generic task " + timeInMillis);
          t.setDescription("some description " + timeInMillis);
          Random r = new Random();
          int hours = r.nextInt(50) + 1;
          t.setEstimatedHours((float) hours);
          return t;
      }*/

  def getDateRoundedToDay: Calendar = {
    val cal = Calendar.getInstance
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal
  }

  @throws[ConnectorException]
  def saveAndLoadAll(connector: NewConnector, task: GTask, rows: List[FieldRow]): List[GTask] = {
    connector.saveData(new PreviouslyCreatedTasksResolver, List(task).asJava, ProgressMonitorUtils.DUMMY_MONITOR, rows.asJava)
    ConnectorUtils.loadDataOrderedById(connector).asScala.toList
  }

  @throws[ConnectorException]
  def saveAndLoadList(connector: NewConnector, tasks: List[GTask], rows: List[FieldRow]): List[GTask] = {
    connector.saveData(new PreviouslyCreatedTasksResolver, tasks.asJava, ProgressMonitorUtils.DUMMY_MONITOR, rows.asJava)
    ConnectorUtils.loadDataOrderedById(connector).asScala.toList
  }

  @throws[ConnectorException]
  def saveAndLoad(connector: NewConnector, task: GTask, rows: util.List[FieldRow]): GTask = {
    val result = connector.saveData(new PreviouslyCreatedTasksResolver, util.Arrays.asList(task), ProgressMonitorUtils.DUMMY_MONITOR, rows)
    val remoteKeys = result.getRemoteKeys
    val remoteKey = remoteKeys.iterator.next.key
    connector.loadTaskByKey(remoteKey, rows)
  }

  /**
    * Load task that was previously created and its result is saved in [[SaveResult]]
    */
  @throws[ConnectorException]
  def loadCreatedTask(connector: NewConnector, rows: util.List[FieldRow], result: SaveResult): GTask = {
    val remoteKey = result.getRemoteKeys.head.key
    connector.loadTaskByKey(remoteKey, rows)
  }

  /**
    * @return the new task Key
    */

  def saveAndLoadViaSummary(connector: NewConnector, task: GTask, rows: List[FieldRow], fieldToSearch:Field): GTask = {
    val loadedTasks = saveAndLoadAll(connector, task, rows)
    findTaskByFieldName(loadedTasks, fieldToSearch.name, task.getValue(fieldToSearch).toString)
  }


  @throws[ConnectorException]
  def save(connector: NewConnector, task: GTask, rows: List[FieldRow]): String = {
    val result = connector.saveData(new PreviouslyCreatedTasksResolver, List(task).asJava, ProgressMonitorUtils.DUMMY_MONITOR, rows.asJava)
    val remoteKeys = result.getRemoteKeys
    remoteKeys.iterator.next.key
  }

  def setTaskStartYearAgo(task: GTask, startDateFieldName: String): Calendar = {
    val yearAgo = getDateRoundedToDay
    yearAgo.add(Calendar.YEAR, -1)
    task.setValue(startDateFieldName, yearAgo.getTime)
    yearAgo
  }

  def getYearAgo: Date = {
    val yearAgo = getDateRoundedToDay
    yearAgo.add(Calendar.YEAR, -1)
    yearAgo.getTime
  }

  def setTaskDueDateNextYear(task: GTask, dueDateFieldName: String): Calendar = {
    val cal = getDateRoundedToDay
    cal.add(Calendar.YEAR, 1)
    task.setValue(dueDateFieldName, cal.getTime)
    cal
  }
}
