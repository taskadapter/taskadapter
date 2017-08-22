package com.taskadapter.connector.testlib

import java.util

import com.taskadapter.connector._
import com.taskadapter.connector.common.ProgressMonitorUtils
import com.taskadapter.connector.definition.{ExportDirection, TaskId}
import com.taskadapter.core.{PreviouslyCreatedTasksCache, PreviouslyCreatedTasksResolver, TaskSaver}
import com.taskadapter.model.{GTask, StandardField}
import org.junit.Assert.{assertEquals, assertFalse}
import org.scalatest.Matchers
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

object CommonTestChecks extends Matchers {
  private val logger = LoggerFactory.getLogger(CommonTestChecks.getClass)

  def skipCleanup(id: TaskId): Unit = {}

  def taskIsCreatedAndLoaded(connector: NewConnector, task: GTask, rows: Seq[FieldRow], fieldNameToSearch: Field,
                             cleanup: TaskId => Unit): Unit = {
    val tasksQty = 1
    val expectedValue = task.getValue(fieldNameToSearch)

    val result = connector.saveData(PreviouslyCreatedTasksResolver.empty, List(task).asJava, ProgressMonitorUtils.DUMMY_MONITOR,
      rows)
    assertEquals(tasksQty, result.getCreatedTasksNumber)

    val createdTask1Id = result.getRemoteKeys.iterator.next()

    val loadedTasks = connector.loadData()
    // there could be some other previously created tasks
    loadedTasks.size() should be >= tasksQty

    val foundTask = TestUtils.findTaskInList(loadedTasks, createdTask1Id)
    foundTask.isDefined shouldBe true
    assertEquals(expectedValue, foundTask.get.getValue(fieldNameToSearch))
    cleanup(createdTask1Id)
  }


  def createsTasks(connector: NewConnector, rows: List[FieldRow], tasks: List[GTask],
                   cleanup: TaskId => Unit): Unit = {
    val result = connector.saveData(PreviouslyCreatedTasksResolver.empty, tasks.asJava, ProgressMonitorUtils.DUMMY_MONITOR, rows)
    assertFalse(result.hasErrors)
    assertEquals(tasks.size, result.getCreatedTasksNumber)
    logger.debug(s"created $result")
    result.getRemoteKeys.foreach(cleanup(_))
  }

  def fieldIsSavedByDefault(connector: NewConnector, task: GTask,
                            suggestedMappings: Map[Field, StandardField],
                            fieldNameToSearch: Field,
                            cleanup: TaskId => Unit): Unit = {
    val mappings = NewConfigSuggester.suggestedFieldMappingsForNewConfig(suggestedMappings, suggestedMappings)
    val rows = MappingBuilder.build(mappings, ExportDirection.RIGHT)
    val loadedTask = TestUtils.saveAndLoadViaSummary(connector, task, rows.toList, fieldNameToSearch)
    assertEquals(task.getValue(fieldNameToSearch), loadedTask.getValue(fieldNameToSearch))
    cleanup(loadedTask.getIdentity)
  }

  def taskCreatedAndUpdatedOK(targetLocation: String, connector: NewConnector, rows: Seq[FieldRow], task: GTask,
                              fieldToChangeInTest: String,
                              newValue: String,
                              cleanup: TaskId => Unit): Unit = {
    // CREATE
    val result = TaskSaver.save(PreviouslyCreatedTasksResolver.empty, connector, "some name", rows, util.Arrays.asList(task), ProgressMonitorUtils.DUMMY_MONITOR)
    assertFalse(result.hasErrors)
    assertEquals(1, result.getCreatedTasksNumber)
    val newTaskId = result.getRemoteKeys.iterator.next()
    val loaded = connector.loadTaskByKey(newTaskId, rows.asJava)

    // UPDATE
    loaded.setValue(fieldToChangeInTest, newValue)
    val resolver = new TaskResolverBuilder(targetLocation).pretend(newTaskId, newTaskId)
    val result2 = TaskSaver.save(resolver, connector, "some name", rows, util.Arrays.asList(loaded), ProgressMonitorUtils.DUMMY_MONITOR)
    assertFalse(result2.hasErrors)
    assertEquals(1, result2.getUpdatedTasksNumber)
    val loadedAgain = connector.loadTaskByKey(newTaskId, rows.asJava)
    assertEquals(newValue, loadedAgain.getValue(fieldToChangeInTest))
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
