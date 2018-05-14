package com.taskadapter.connector.jira

import com.google.common.collect.Iterables
import com.taskadapter.connector.common.ProgressMonitorUtils
import com.taskadapter.connector.definition.TaskId
import com.taskadapter.connector.testlib.{CommonTestChecks, TestUtils}
import com.taskadapter.core.PreviouslyCreatedTasksResolver
import com.taskadapter.model._
import org.fest.assertions.Assertions.assertThat
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSpec, Matchers}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

@RunWith(classOf[JUnitRunner])
class JiraTest extends FunSpec with Matchers with BeforeAndAfter with BeforeAndAfterAll {

  private val logger = LoggerFactory.getLogger(classOf[JiraTest])

  private var config = JiraPropertiesLoader.createTestConfig
  private val setup = JiraPropertiesLoader.getTestServerInfo
  private var client = JiraConnectionFactory.createClient(setup)
  private var connector = new JiraConnector(config, setup)

  logger.info("Running JIRA tests using: " + setup.host)


  describe("Create") {
    it("connector does not fail empty tasks list") {
      connector.saveData(PreviouslyCreatedTasksResolver.empty, List[GTask]().asJava, ProgressMonitorUtils.DUMMY_MONITOR, JiraFieldBuilder.getDefault())
    }

    it("tasks are created without errors") {
      CommonTestChecks.createsTasks(connector, JiraFieldBuilder.getDefault(), JiraGTaskBuilder.getTwo(),
        id => TestJiraClientHelper.deleteTasks(client, id))
    }

    it("assignee and reporter are set") {
      val userPromise = client.getUserClient.getUser(setup.userName)
      val jiraUser = userPromise.claim
      val task = new GTask
      task.setValue(Summary, "some")
      val user = new GUser(null, jiraUser.getName, jiraUser.getDisplayName)
      task.setValue(Assignee, user)
      task.setValue(Reporter, user)
      val loadedTask = TestUtils.saveAndLoad(connector, task, JiraFieldBuilder.getDefault())
      loadedTask.getValue(Assignee).getLoginName shouldBe jiraUser.getName
      loadedTask.getValue(Assignee).getDisplayName shouldBe jiraUser.getDisplayName

      loadedTask.getValue(Reporter).getLoginName shouldBe jiraUser.getName
      loadedTask.getValue(Reporter).getDisplayName shouldBe jiraUser.getDisplayName

      TestJiraClientHelper.deleteTasks(client, loadedTask.getIdentity)
    }

    it("status is set on create") {
      CommonTestChecks.taskIsCreatedAndLoaded(connector,
        new GTaskBuilder()
          .withRandom(Summary)
          .withField(TaskStatus, "In Progress")
          .build(),
        JiraFieldBuilder.withStatus(),
        TaskStatus,
        id => TestJiraClientHelper.deleteTasks(client, id))
    }
  }

  describe("Update") {
    it("task is updated") {
      CommonTestChecks.taskCreatedAndUpdatedOK(setup.host,
        connector, JiraFieldBuilder.getDefault(),
        JiraGTaskBuilder.withSummary(), Summary, "new value",
        id => TestJiraClientHelper.deleteTasks(client, id))
    }

    it("changes status to In Progress") {
      CommonTestChecks.taskCreatedAndUpdatedOK(setup.host,
        connector, JiraFieldBuilder.withStatus(),
        GTaskBuilder.withRandom(Summary),
        TaskStatus, "In Progress",
        id => TestJiraClientHelper.deleteTasks(client, id))
    }
  }

  it("testGetIssuesByProject") {
    val tasks = generateTasks
    connector.saveData(PreviouslyCreatedTasksResolver.empty, tasks.asJava, ProgressMonitorUtils.DUMMY_MONITOR, JiraFieldBuilder.getDefault())
    val jql = JqlBuilder.findIssuesByProject(config.getProjectKey)
    val issues = JiraClientHelper.findIssues(client, jql)
    assertThat(Iterables.size(issues)).isGreaterThan(1)
  }

  it("two issues linked") {
    config.setSaveIssueRelations(true)
    val list = generateTasks
    val task1 = list(0)
    val task2 = list(1)
    task1.getRelations.add(GRelation(TaskId(task1.getId, task1.getKey),
      TaskId(task2.getId, task2.getKey), Precedes))
    TestUtils.saveAndLoadList(connector, list, JiraFieldBuilder.getDefault())
    val issues = TestJiraClientHelper.findIssuesBySummary(client, task1.getValue(Summary))
    val createdIssue1 = issues.iterator.next
    val links = createdIssue1.getIssueLinks
    assertEquals(1, Iterables.size(links))
    val link = links.iterator.next
    val targetIssueKey = link.getTargetIssueKey
    val createdIssue2 = TestJiraClientHelper.findIssuesBySummary(client, task2.getValue(Summary)).iterator.next
    assertEquals(createdIssue2.getKey, targetIssueKey)
    TestJiraClientHelper.deleteTasks(client, TaskId(createdIssue1.getId, createdIssue1.getKey),
      TaskId(createdIssue2.getId, createdIssue2.getKey))
  }

  private def generateTasks = {
    val task1 = JiraGTaskBuilder.withSummary()
    task1.setId(1l)
    task1.setKey("1")
    val task2 = JiraGTaskBuilder.withSummary()
    task2.setId(2l)
    task2.setKey("2")
    List(task1, task2)
  }
}
