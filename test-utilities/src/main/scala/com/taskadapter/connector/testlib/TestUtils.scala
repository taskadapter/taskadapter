package com.taskadapter.connector.testlib

import java.util
import java.util.{Calendar, Date}

import com.taskadapter.connector.common.ProgressMonitorUtils
import com.taskadapter.connector.definition.exceptions.ConnectorException
import com.taskadapter.connector.definition.{SaveResult, TaskId}
import com.taskadapter.connector.{Field, FieldRow, NewConnector}
import com.taskadapter.core.{PreviouslyCreatedTasksResolver, TaskLoader}
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

  def findTaskInList(list: util.List[GTask], createdTaskId: TaskId): Option[GTask] = {
    list.asScala.find(_.getIdentity == createdTaskId)
  }

  def findTaskByKey(list: util.List[GTask], key: String): GTask = {
    list.asScala.find(_.getKey == key).orNull
  }

  def findTaskByFieldName(list: Seq[GTask], fieldName: String, value: String): GTask = {
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
    connector.saveData(PreviouslyCreatedTasksResolver.empty, List(task).asJava, ProgressMonitorUtils.DUMMY_MONITOR, rows)
    connector.loadData().asScala.sortBy(_.getId).toList
  }

  @throws[ConnectorException]
  def saveAndLoadList(connector: NewConnector, tasks: Seq[GTask], rows: Seq[FieldRow]): List[GTask] = {
    connector.saveData(PreviouslyCreatedTasksResolver.empty, tasks.asJava, ProgressMonitorUtils.DUMMY_MONITOR, rows)
    connector.loadData().asScala.sortBy(_.getId).toList
  }

  @throws[ConnectorException]
  def saveAndLoad(connector: NewConnector, task: GTask, rows: Seq[FieldRow]): GTask = {
    val result = connector.saveData(PreviouslyCreatedTasksResolver.empty, util.Arrays.asList(task), ProgressMonitorUtils.DUMMY_MONITOR, rows)
    val remoteKeys = result.getRemoteKeys
    val remoteKey = remoteKeys.iterator.next
    connector.loadTaskByKey(remoteKey, rows.asJava)
  }

  /**
    * Load task that was previously created and its result is saved in [[SaveResult]]
    */
  @throws[ConnectorException]
  def loadCreatedTask(connector: NewConnector, rows: util.List[FieldRow], result: SaveResult): GTask = {
    val remoteKey = result.getRemoteKeys.head
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
  def save(connector: NewConnector, task: GTask, rows: List[FieldRow]): TaskId = {
    val result = connector.saveData(PreviouslyCreatedTasksResolver.empty, List(task).asJava, ProgressMonitorUtils.DUMMY_MONITOR, rows)
    val remoteKeys = result.getRemoteKeys
    remoteKeys.iterator.next
  }

  /**
    * @param rows source-target field rows
    */
  def loadAndSave(sourceConnector: NewConnector, targetConnector: NewConnector,
                  rows: Seq[FieldRow]): GTask = {
    val loadedTask = TaskLoader.loadTasks(1, sourceConnector, "sourceName", ProgressMonitorUtils.DUMMY_MONITOR).asScala.toList.head
    val result = TestUtils.saveAndLoadList(targetConnector, Seq(loadedTask), rows).head
    result
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
