package com.taskadapter.connector.jira

import java.util

import com.taskadapter.connector.FieldRow
import com.taskadapter.connector.common.ProgressMonitorUtils
import com.taskadapter.connector.testlib.TestUtils
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
    val loadedTask = connector.loadTaskByKey(key, JiraFieldBuilder.getDefault)
    assertThat(loadedTask.getValue(JiraField.summary)).isEqualTo(summary)
  }

  /*
    it("descriptionSavedByDefault") {
      CommonTests.descriptionSavedByDefault(getConnector, JiraFieldBuilder.getDefault())
    }
  */

  it("subtasksAreCreated") {
    val parentTask = new JiraGTaskBuilder("parent task").withId(11).build()

    val subTask1 = new JiraGTaskBuilder("child task 1").withId(22).build()
    val subTask2 = new JiraGTaskBuilder("child task 2").withId(33).build()
    parentTask.getChildren.addAll(List(subTask1, subTask2).asJava)
    val connector = getConnector
    val result = connector.saveData(util.Arrays.asList(parentTask), ProgressMonitorUtils.DUMMY_MONITOR, JiraFieldBuilder.getDefault)
    assertThat(result.getCreatedTasksNumber).isEqualTo(3)
    val parentKey = result.getRemoteKey(11)
    val subTask1RemoteKey = result.getRemoteKey(22)
    val subTask2RemoteKey = result.getRemoteKey(33)

    val loadedSubTask1 = connector.loadTaskByKey(subTask1RemoteKey, JiraFieldBuilder.getDefault)
    val loadedSubTask2 = connector.loadTaskByKey(subTask2RemoteKey, JiraFieldBuilder.getDefault)
    assertThat(loadedSubTask1.getParentKey).isEqualTo(parentKey)
    assertThat(loadedSubTask2.getParentKey).isEqualTo(parentKey)

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
  it("taskIsCreatedWithDefaultDescriptionField") {
    // description is empty so that the default value will be set later
    val task = JiraGTaskBuilder.withSummary
    val connector = getConnector

    val rows = List(
      FieldRow(true, JiraField.summary, JiraField.summary, ""),
      FieldRow(true, JiraField.description, JiraField.description, "some default")
    )

    val result = connector.saveData(util.Arrays.asList(task), ProgressMonitorUtils.DUMMY_MONITOR, rows.asJava)
    assertThat(result.getCreatedTasksNumber).isEqualTo(1)
    // TODO this is ugly
    val values = result.getIdToRemoteKeyMap.values
    val key = values.iterator.next
    val loadedTask = connector.loadTaskByKey(key, rows.asJava)
    assertThat(loadedTask.getValue(JiraField.description)).isEqualTo("some default")
  }

  private def getConnector = new JiraConnector(JiraPropertiesLoader.createTestConfig)
}
