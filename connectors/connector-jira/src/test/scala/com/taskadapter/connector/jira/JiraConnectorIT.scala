package com.taskadapter.connector.jira

import java.util

import com.taskadapter.connector.FieldRow
import com.taskadapter.connector.common.ProgressMonitorUtils
import com.taskadapter.connector.definition.TaskId
import com.taskadapter.connector.testlib.{CommonTestChecks, TestUtils}
import com.taskadapter.core.PreviouslyCreatedTasksResolver
import com.taskadapter.model._
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
    val loadedTask = connector.loadTaskByKey(id, JiraFieldBuilder.getDefault)
    assertThat(loadedTask.getValue(Summary)).isEqualTo(summary)
    TestJiraClientHelper.deleteTasks(client, loadedTask.getIdentity)
  }

  it("description saved by default") {
    CommonTestChecks.fieldIsSavedByDefault(getConnector,
      new JiraGTaskBuilder().withDescription().build(),
      JiraField.defaultFieldsForNewConfig,
      Description,
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
      JiraFieldBuilder.getDefault)
    assertThat(result.createdTasksNumber).isEqualTo(3)
    val parentTaskId = result.keyToRemoteKeyList.head._2
    val subTask1Id = result.keyToRemoteKeyList(1)._2
    val subTask2Id = result.keyToRemoteKeyList(2)._2

    val loadedSubTask1 = connector.loadTaskByKey(subTask1Id, JiraFieldBuilder.getDefault)
    val loadedSubTask2 = connector.loadTaskByKey(subTask2Id, JiraFieldBuilder.getDefault)
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
      FieldRow(Summary, Summary, ""),
      FieldRow(Description, Description, "some default")
    )

    val result = connector.saveData(PreviouslyCreatedTasksResolver.empty, util.Arrays.asList(task), ProgressMonitorUtils.DUMMY_MONITOR, rows)
    assertThat(result.createdTasksNumber).isEqualTo(1)
    val taskId = result.keyToRemoteKeyList.head._2
    val loadedTask = connector.loadTaskByKey(taskId, rows)
    assertThat(loadedTask.getValue(Description)).isEqualTo("some default")
    TestJiraClientHelper.deleteTasks(client, loadedTask.getIdentity)
  }

  it("assignee is saved and loaded") {
    val task = new JiraGTaskBuilder().build().setValue(Assignee, GUser(null, "user", "full name"))
    val id = TestUtils.save(getConnector, task, JiraFieldBuilder.getDefault)
    val loadedTask = getConnector.loadTaskByKey(id, JiraFieldBuilder.getDefault)
    assertThat(loadedTask.getValue(Assignee).loginName).isEqualTo("user")
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
    val field = CustomSeqString("custom_checkbox_1")
    task.setValue(field, Seq("option1", "option2"))
    val rows = JiraFieldBuilder.getDefault() ++ List(
      FieldRow(field, field, "")
    )
    val id = TestUtils.save(getConnector, task, rows)
    val loadedTask = getConnector.loadTaskByKey(id, rows)
    loadedTask.getValue(field) should contain only ("option1", "option2")
    TestJiraClientHelper.deleteTasks(client, loadedTask.getIdentity)
  }

  private def getConnector = new JiraConnector(JiraPropertiesLoader.createTestConfig, JiraPropertiesLoader.getTestServerInfo)
}
