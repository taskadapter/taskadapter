package com.taskadapter.connector.testlib

import java.util

import com.taskadapter.connector._
import com.taskadapter.connector.common.ProgressMonitorUtils
import com.taskadapter.connector.definition.{ExportDirection, TaskId}
import com.taskadapter.core.{PreviouslyCreatedTasksCache, PreviouslyCreatedTasksResolver, TaskSaver}
import com.taskadapter.model.{Field, GTask}
import org.junit.Assert.{assertEquals, assertFalse}
import org.scalatest.Matchers
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

object CommonTestChecks extends Matchers {
  private val logger = LoggerFactory.getLogger(CommonTestChecks.getClass)

  def skipCleanup(id: TaskId): Unit = {}

  def taskIsCreatedAndLoaded(connector: NewConnector, task: GTask, rows: Seq[FieldRow[_]], fields: Seq[Field[_]],
                             cleanup: TaskId => Unit): Unit = {
    val tasksQty = 1

    val result = connector.saveData(PreviouslyCreatedTasksResolver.empty, List(task).asJava, ProgressMonitorUtils.DUMMY_MONITOR,
      rows)
    assertEquals(tasksQty, result.createdTasksNumber)

    val createdTask1Id = result.getRemoteKeys.iterator.next()

    val loadedTasks = connector.loadData()
    // there could be some other previously created tasks
    loadedTasks.size() should be >= tasksQty

    val foundTask = TestUtils.findTaskInList(loadedTasks, createdTask1Id)
    foundTask.isDefined shouldBe true

    // check all requested fields
    fields.foreach(f=> foundTask.get.getValue(f) shouldBe task.getValue(f))

    cleanup(createdTask1Id)
  }


  def createsTasks(connector: NewConnector, rows: List[FieldRow[_]], tasks: List[GTask],
                   cleanup: TaskId => Unit): Unit = {
    val result = connector.saveData(PreviouslyCreatedTasksResolver.empty, tasks.asJava, ProgressMonitorUtils.DUMMY_MONITOR, rows)
    assertFalse(result.hasErrors)
    assertEquals(tasks.size, result.createdTasksNumber)
    logger.debug(s"created $result")
    result.getRemoteKeys.foreach(cleanup(_))
  }

  def fieldIsSavedByDefault(connector: NewConnector, task: GTask,
                            suggestedMappings: Seq[Field[_]],
                            fieldToSearch: Field[_],
                            cleanup: TaskId => Unit): Unit = {
    val mappings = NewConfigSuggester.suggestedFieldMappingsForNewConfig(suggestedMappings, suggestedMappings)
    val rows = MappingBuilder.build(mappings, ExportDirection.RIGHT)
    val loadedTask = TestUtils.saveAndLoadViaSummary(connector, task, rows.toList, fieldToSearch)
    assertEquals(task.getValue(fieldToSearch), loadedTask.getValue(fieldToSearch))
    cleanup(loadedTask.getIdentity)
  }

  def taskCreatedAndUpdatedOK[T](targetLocation: String, connector: NewConnector, rows: Seq[FieldRow[_]], task: GTask,
                                 fieldToChangeInTest: Field[T],
                                 newValue: T,
                                 cleanup: TaskId => Unit): Unit = {
    taskCreatedAndUpdatedOK(targetLocation, connector, rows, task, Seq((fieldToChangeInTest, newValue)), cleanup)
  }

  def taskCreatedAndUpdatedOK[T](targetLocation: String, connector: NewConnector, rows: Seq[FieldRow[_]], task: GTask,
                                 toUpdate: Seq[(Field[T], T)],
                                 cleanup: TaskId => Unit): Unit = {

    // CREATE
    val result = TaskSaver.save(PreviouslyCreatedTasksResolver.empty, connector, "some name", rows, util.Arrays.asList(task), ProgressMonitorUtils.DUMMY_MONITOR)
    assertFalse(s"must not have any errors, but got ${result.taskErrors}", result.hasErrors)
    assertEquals(1, result.createdTasksNumber)
    val newTaskId = result.getRemoteKeys.iterator.next()
    val loaded = connector.loadTaskByKey(newTaskId, rows)

    // UPDATE all requested fields
    toUpdate.foreach(i => loaded.setValue(i._1, i._2))

    val resolver = new TaskResolverBuilder(targetLocation).pretend(newTaskId, newTaskId)
    val result2 = TaskSaver.save(resolver, connector, "some name", rows, util.Arrays.asList(loaded), ProgressMonitorUtils.DUMMY_MONITOR)
    assertFalse(result2.hasErrors)
    assertEquals(1, result2.updatedTasksNumber)
    val loadedAgain = connector.loadTaskByKey(newTaskId, rows)

    toUpdate.foreach(i => assertEquals(i._2, loadedAgain.getValue(i._1)))

    cleanup(loaded.getIdentity)
  }

  class TaskResolverBuilder(targetLocation: String) {
    def pretend(id1: TaskId, id2: TaskId): PreviouslyCreatedTasksResolver = {
      new PreviouslyCreatedTasksResolver(
        PreviouslyCreatedTasksCache("1", targetLocation, Seq(
          (id1, id2)
        ))
      )
    }
  }

}

case class ITFixture(targetLocation: String, connector: NewConnector, cleanup: TaskId => Unit) {
  def taskIsCreatedAndLoaded(task: GTask, fieldNameToSearch: Field[_]): Unit = {
    taskIsCreatedAndLoaded(task, Seq(fieldNameToSearch))
  }
  def taskIsCreatedAndLoaded(task: GTask, fields: Seq[Field[_]]): Unit = {
    CommonTestChecks.taskIsCreatedAndLoaded(connector, task, FieldRowBuilder.rows(fields), fields, cleanup)
  }

  def taskCreatedAndUpdatedOK[T](rows: Seq[FieldRow[_]], task: GTask, fieldToChangeInTest: Field[T], newValue: T): Unit = {
    CommonTestChecks.taskCreatedAndUpdatedOK(targetLocation, connector, rows, task, fieldToChangeInTest, newValue, cleanup)
  }

  def taskCreatedAndUpdatedOK[T](task: GTask, toUpdate: Seq[(Field[T], T)]): Unit = {
    CommonTestChecks.taskCreatedAndUpdatedOK(targetLocation, connector,
      FieldRowBuilder.rows(toUpdate.map(_._1)),
      task, toUpdate, cleanup)
  }
}