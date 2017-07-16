package com.taskadapter.connector.jira

import java.util

import com.taskadapter.connector.FieldRow
import com.taskadapter.connector.common.ProgressMonitorUtils
import com.taskadapter.connector.testlib.{CommonTestChecks, InMemoryTaskKeeper, TestUtils}
import org.fest.assertions.Assertions.assertThat
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSpec, Matchers}

import scala.collection.JavaConverters._

@RunWith(classOf[JUnitRunner])
class JiraConnectorIT extends FunSpec with Matchers with BeforeAndAfter with BeforeAndAfterAll {

  it("testLoadTaskByKey") {
    val connector = getConnector
    val summary = "load by key"
    val task = new JiraGTaskBuilder(summary).withType("Task").build
    val key = TestUtils.save(connector, task, JiraFieldBuilder.getDefault)
    val loadedTask = connector.loadTaskByKey(key, JiraFieldBuilder.getDefault.asJava)
    assertThat(loadedTask.getValue(JiraField.summary)).isEqualTo(summary)
  }

  it("description saved by default") {
    CommonTestChecks.descriptionSavedByDefault(getConnector,
      new JiraGTaskBuilder().withDescription().build(),
      JiraField.getSuggestedCombinations(),
      JiraField.description)
  }

  it("subtasks are created") {
    val parentTask = new JiraGTaskBuilder("parent task").build()

    val subTask1 = new JiraGTaskBuilder("child task 1").build()
    val subTask2 = new JiraGTaskBuilder("child task 2").build()
    parentTask.getChildren.addAll(List(subTask1, subTask2).asJava)
    val connector = getConnector
    val result = connector.saveData(new InMemoryTaskKeeper(), util.Arrays.asList(parentTask),
      ProgressMonitorUtils.DUMMY_MONITOR,
      JiraFieldBuilder.getDefault.asJava)
    assertThat(result.getCreatedTasksNumber).isEqualTo(3)
    val parentTaskId = result.getIdToRemoteKeyList.head._2
    val subTask1Id = result.getIdToRemoteKeyList(1)._2
    val subTask2Id = result.getIdToRemoteKeyList(2)._2

    val loadedSubTask1 = connector.loadTaskByKey(subTask1Id.key, JiraFieldBuilder.getDefault.asJava)
    val loadedSubTask2 = connector.loadTaskByKey(subTask2Id.key, JiraFieldBuilder.getDefault.asJava)
    assertThat(loadedSubTask1.getParentIdentity).isEqualTo(parentTaskId)
    assertThat(loadedSubTask2.getParentIdentity).isEqualTo(parentTaskId)

    // TODO need to delete the temporary tasks
  }

  /*
  @Ignore("This test requires a custom project configuration: project with 'ENV' key")
  it("taskIsCreatedInProjectWithRequiredEnvironmentField") {
    val task = TestUtils.generateTask
    task.setType("Bug")
    val environmentString = "some environment"
    task.setEnvironment(environmentString)
    // special project with Environment set as a required field
    testConfig.setProjectKey("ENV")
    val connector = new JiraConnector(testConfig)
    val result = connector.saveData(util.Arrays.asList(task), null, TEST_MAPPINGS)
    assertThat(result.getCreatedTasksNumber).isEqualTo(1)
    // TODO this is ugly
    val values = result.getIdToRemoteKeyMap.values
    val key = values.iterator.next
    val loadedTask = connector.loadTaskByKey(key, new Mappings)
    assertThat(loadedTask.getEnvironment).isEqualTo(environmentString)
  }
*/

  // TODO move to some generic tests, this is not Jira-specific
  it("task created with default description field") {
    // description is empty so that the default value will be set later
    val task = JiraGTaskBuilder.withSummary()
    val connector = getConnector

    val rows = List(
      FieldRow(JiraField.summary, JiraField.summary, ""),
      FieldRow(JiraField.description, JiraField.description, "some default")
    )

    val result = connector.saveData(new InMemoryTaskKeeper(), util.Arrays.asList(task), ProgressMonitorUtils.DUMMY_MONITOR, rows.asJava)
    assertThat(result.getCreatedTasksNumber).isEqualTo(1)
    val taskId = result.getIdToRemoteKeyList.head._2
    val loadedTask = connector.loadTaskByKey(taskId.key, rows.asJava)
    assertThat(loadedTask.getValue(JiraField.description)).isEqualTo("some default")
  }

  private def getConnector = new JiraConnector(JiraPropertiesLoader.createTestConfig)
}
