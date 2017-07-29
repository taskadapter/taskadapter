package com.taskadapter.connector.testlib

import java.util

import com.taskadapter.connector._
import com.taskadapter.connector.common.ProgressMonitorUtils
import com.taskadapter.connector.definition.{ExportDirection, TaskId}
import com.taskadapter.core.{PreviouslyCreatedTasksResolver, TaskSaver}
import com.taskadapter.model.{GTask, StandardField}
import org.junit.Assert.{assertEquals, assertFalse}

object CommonTestChecks {
  def skipCleanup(id: TaskId): Unit = {}

  // TODO TA3 fix
  /*    public static void testLoadTasks(NewConnector connector, List<FieldRow> rows) throws ConnectorException {
          int tasksQty = 1;
          List<GTask> tasks = TestUtils.generateTasks(tasksQty);

          String expectedSummaryTask1 = tasks.get(0).getSummary();
          Integer expectedID = tasks.get(0).getId();

          TaskSaveResult result = connector.saveData(tasks, ProgressMonitorUtils.DUMMY_MONITOR, rows);
          assertEquals(tasksQty, result.getCreatedTasksNumber());

          Integer createdTask1Id = Integer.valueOf(result.getIdToRemoteKeyMap().get(expectedID));

          List<GTask> loadedTasks = ConnectorUtils.loadDataOrderedById(connector, rows);
          // there could be some other previously created tasks
          assertTrue(loadedTasks.size() >= tasksQty);

          GTask foundTask = TestUtils.findTaskInList(loadedTasks, createdTask1Id);
          assertNotNull(foundTask);
          assertEquals(expectedSummaryTask1, foundTask.getSummary());
      }

  */

  def createsTasks(connector: NewConnector, rows: util.List[FieldRow], tasks: util.List[GTask],
                   cleanup: TaskId => Unit): Unit = {
    val result = connector.saveData(PreviouslyCreatedTasksResolver.empty, tasks, ProgressMonitorUtils.DUMMY_MONITOR, rows)
    assertFalse(result.hasErrors)
    assertEquals(tasks.size, result.getCreatedTasksNumber)
    result.getRemoteKeys.foreach(cleanup(_))
  }

  def descriptionSavedByDefault(connector: NewConnector, task: GTask,
                                suggestedMappings: Map[Field, StandardField],
                                fieldNameToSearch: Field,
                                cleanup: TaskId => Unit): Unit = {
    val mappings = NewConfigSuggester.suggestedFieldMappingsForNewConfig(suggestedMappings, suggestedMappings)
    val rows = MappingBuilder.build(mappings, ExportDirection.RIGHT)
    val loadedTask = TestUtils.saveAndLoadViaSummary(connector, task, rows.toList, fieldNameToSearch)
    assertEquals(task.getValue(fieldNameToSearch), loadedTask.getValue(fieldNameToSearch))
    cleanup(loadedTask.getIdentity)
  }

  def taskCreatedAndUpdatedOK(connector: NewConnector, rows: util.List[FieldRow], task: GTask, fieldToChangeInTest: String,
                              cleanup: TaskId => Unit): Unit = {
    val id = task.getId
    // CREATE

    val result = TaskSaver.save(PreviouslyCreatedTasksResolver.empty, connector, "some name", rows, util.Arrays.asList(task), ProgressMonitorUtils.DUMMY_MONITOR)
    assertFalse(result.hasErrors)
    assertEquals(1, result.getCreatedTasksNumber)
    val remoteKey = result.getRemoteKey(id)
    val loaded = connector.loadTaskByKey(remoteKey, rows)
    // UPDATE
    val newValue = "some new text"
    loaded.setValue(fieldToChangeInTest, newValue)
    loaded.setKey(remoteKey.key)
    val result2 = TaskSaver.save(PreviouslyCreatedTasksResolver.empty, connector, "some name", rows, util.Arrays.asList(loaded), ProgressMonitorUtils.DUMMY_MONITOR)
    assertFalse(result2.hasErrors)
    assertEquals(1, result2.getUpdatedTasksNumber)
    val loadedAgain = connector.loadTaskByKey(remoteKey, rows)
    assertEquals(newValue, loadedAgain.getValue(fieldToChangeInTest))
    cleanup(loaded.getIdentity)
  }
}
