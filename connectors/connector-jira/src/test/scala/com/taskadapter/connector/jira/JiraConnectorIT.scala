package com.taskadapter.connector.jira

import java.util

import com.taskadapter.connector.{Field, FieldRow}
import com.taskadapter.connector.common.ProgressMonitorUtils
import com.taskadapter.connector.definition.TaskId
import com.taskadapter.connector.testlib.{CommonTestChecks, TestUtils}
import com.taskadapter.core.PreviouslyCreatedTasksResolver
import org.fest.assertions.Assertions.assertThat
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSpec, Matchers}

import scala.collection.JavaConverters._

@RunWith(classOf[JUnitRunner])
class JiraConnectorIT extends FunSpec with Matchers with BeforeAndAfter with BeforeAndAfterAll {
  private val webServerInfo = JiraPropertiesLoader.getTestServerInfo
  private var client = JiraConnectionFactory.createClient(webServerInfo)

  it("testLoadTaskByKey") {
    val connector = getConnector
    val summary = "load by key"
    val task = new JiraGTaskBuilder(summary).withType("Task").build
    val id = TestUtils.save(connector, task, JiraFieldBuilder.getDefault)
    val loadedTask = connector.loadTaskByKey(id, JiraFieldBuilder.getDefault.asJava)
    assertThat(loadedTask.getValue(JiraField.summary)).isEqualTo(summary)
    TestJiraClientHelper.deleteTasks(client, loadedTask.getIdentity)
  }

  it("description saved by default") {
    CommonTestChecks.descriptionSavedByDefault(getConnector,
      new JiraGTaskBuilder().withDescription().build(),
      JiraField.getSuggestedCombinations(),
      JiraField.description,
      taskId => TestJiraClientHelper.deleteTasks(client, taskId))
  }

  it("subtasks are created") {
    val parentTask = new JiraGTaskBuilder("parent task").build()

    val subTask1 = new JiraGTaskBuilder("child task 1").build()
    val subTask2 = new JiraGTaskBuilder("child task 2").build()
    parentTask.getChildren.addAll(List(subTask1, subTask2).asJava)
    val connector = getConnector
    val result = connector.saveData(PreviouslyCreatedTasksResolver.empty, util.Arrays.asList(parentTask),
      ProgressMonitorUtils.DUMMY_MONITOR,
      JiraFieldBuilder.getDefault.asJava)
    assertThat(result.getCreatedTasksNumber).isEqualTo(3)
    val parentTaskId = result.getIdToRemoteKeyList.head._2
    val subTask1Id = result.getIdToRemoteKeyList(1)._2
    val subTask2Id = result.getIdToRemoteKeyList(2)._2

    val loadedSubTask1 = connector.loadTaskByKey(subTask1Id, JiraFieldBuilder.getDefault.asJava)
    val loadedSubTask2 = connector.loadTaskByKey(subTask2Id, JiraFieldBuilder.getDefault.asJava)
    assertThat(loadedSubTask1.getParentIdentity).isEqualTo(parentTaskId)
    assertThat(loadedSubTask2.getParentIdentity).isEqualTo(parentTaskId)

    TestJiraClientHelper.deleteTasks(client, loadedSubTask1.getIdentity, loadedSubTask2.getIdentity, parentTaskId)
  }

  // TODO move to some generic tests, this is not Jira-specific
  it("task created with default description field") {
    // description is empty so that the default value will be set later
    val task = JiraGTaskBuilder.withSummary()
    val connector = getConnector

    val rows = List(
      FieldRow(JiraField.summary, JiraField.summary, ""),
      FieldRow(JiraField.description, JiraField.description, "some default")
    )

    val result = connector.saveData(PreviouslyCreatedTasksResolver.empty, util.Arrays.asList(task), ProgressMonitorUtils.DUMMY_MONITOR, rows.asJava)
    assertThat(result.getCreatedTasksNumber).isEqualTo(1)
    val taskId = result.getIdToRemoteKeyList.head._2
    val loadedTask = connector.loadTaskByKey(taskId, rows.asJava)
    assertThat(loadedTask.getValue(JiraField.description)).isEqualTo("some default")
    TestJiraClientHelper.deleteTasks(client, loadedTask.getIdentity)
  }

  /*
    * This test requires a pre-created custom field in your JIRA.
    *
    * - name: custom_checkbox_1
    * - type: checkbox multi-select
    * - allowed values: "option1", "option2"
    */
  it("task is created with multi-value custom field of type option (checkboxes)") {
    val task = JiraGTaskBuilder.withSummary()
    task.setValue("custom_checkbox_1", List("option1", "option2"))
    val rows = JiraFieldBuilder.getDefault() ++ List(
      FieldRow(new Field("", "custom_checkbox_1"), new Field("", "custom_checkbox_1"), "")
    )

    CommonTestChecks.createsTasks(getConnector, rows.asJava,
      List(task).asJava,
      id => TestJiraClientHelper.deleteTasks(client, id))
  }

  private def getConnector = new JiraConnector(JiraPropertiesLoader.createTestConfig, JiraPropertiesLoader.getTestServerInfo)
}
