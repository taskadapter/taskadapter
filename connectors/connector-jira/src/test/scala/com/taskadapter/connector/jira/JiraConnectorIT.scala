package com.taskadapter.connector.jira

import java.util

import com.taskadapter.connector.FieldRow
import com.taskadapter.connector.common.ProgressMonitorUtils
import com.taskadapter.connector.testlib.{CommonTestChecks, FieldRowBuilder, StatefulTestTaskSaver, TestUtilsJava}
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
    val id = TestUtilsJava.save(connector, task, JiraFieldBuilder.getDefault.asJava)
    val loadedTask = connector.loadTaskByKey(id, JiraFieldBuilder.getDefault.asJava)
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
      JiraFieldBuilder.getDefault.asJava)
    assertThat(result.createdTasksNumber).isEqualTo(3)
    val parentTaskId = result.keyToRemoteKeyList.head.newId
    val subTask1Id = result.keyToRemoteKeyList(1).newId
    val subTask2Id = result.keyToRemoteKeyList(2).newId

    val loadedSubTask1 = connector.loadTaskByKey(subTask1Id, JiraFieldBuilder.getDefault.asJava)
    val loadedSubTask2 = connector.loadTaskByKey(subTask2Id, JiraFieldBuilder.getDefault.asJava)
    assertThat(loadedSubTask1.getParentIdentity).isEqualTo(parentTaskId)
    assertThat(loadedSubTask2.getParentIdentity).isEqualTo(parentTaskId)

    TestJiraClientHelper.deleteTasks(client, loadedSubTask1.getIdentity, loadedSubTask2.getIdentity, parentTaskId)
  }

  it("assignee is saved and loaded") {
    val task = new JiraGTaskBuilder().build().setValue(AssigneeLoginName, "user")
    val id = TestUtilsJava.save(getConnector, task, JiraFieldBuilder.getDefault.asJava)
    val loadedTask = getConnector.loadTaskByKey(id, JiraFieldBuilder.getDefault.asJava)
    loadedTask.getValue(AssigneeLoginName) shouldBe "user"
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
    val task = GTaskBuilder.withSummary()
    val field = CustomSeqString("custom_checkbox_1")
    task.setValue(field, Seq("option1", "option2"))
    val rows = JiraFieldBuilder.getDefault() ++ List(
      FieldRow(field, field, "")
    )
    val id = TestUtilsJava.save(getConnector, task, rows.asJava)
    val loadedTask = getConnector.loadTaskByKey(id, rows.asJava)
    loadedTask.getValue(field) should contain only ("option1", "option2")
    TestJiraClientHelper.deleteTasks(client, loadedTask.getIdentity)
  }

  private val rows: Seq[FieldRow[_]] = FieldRowBuilder.rows(Seq(Summary, TaskType))

  describe("Create") {
    it("task is created with default task type set in config") {
      val created = TestUtilsJava.saveAndLoad(getConnector,
        new GTaskBuilder().withRandom(Summary)/*.withField(TaskType, "Story")*/.build(),
        rows.asJava)
      created.getValue(TaskType) shouldBe config.getDefaultTaskType
      TestJiraClientHelper.deleteTasks(client, created.getIdentity)
    }
    it("new task gets requested type") {
      val created = TestUtilsJava.saveAndLoad(getConnector, task("Story"), rows.asJava)
      created.getValue(TaskType) shouldBe "Story"
      TestJiraClientHelper.deleteTasks(client, created.getIdentity)
    }

  }

  private def task(taskTypeName: String): GTask = {
    GTaskBuilder.withRandom(Summary).setValue(TaskType, taskTypeName)
  }

  describe("Update") {
    it("does not reset task type to config default") {
      val saver = new StatefulTestTaskSaver(getConnector, JiraPropertiesLoader.getTestServerInfo.host)
      // regression test
      val created = saver.saveAndLoad(task("Story"), rows.asJava)
      created.setValue(TaskType, null)
      val updated = saver.saveAndLoad(created, rows.asJava)
      updated.getValue(TaskType) shouldBe "Story"
      TestJiraClientHelper.deleteTasks(client, created.getIdentity)
    }
  }

  private val config: JiraConfig = JiraPropertiesLoader.createTestConfig

  private def getConnector = new JiraConnector(config, JiraPropertiesLoader.getTestServerInfo)
}
