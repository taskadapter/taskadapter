package com.taskadapter.connector.jira

import com.google.common.collect.Iterables
import com.taskadapter.connector.common.ProgressMonitorUtils
import com.taskadapter.connector.definition.TaskId
import com.taskadapter.connector.testlib.{CommonTestChecks, ITFixture, TestUtils}
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

  private val fixture = ITFixture(setup.host, connector, id => TestJiraClientHelper.deleteTasks(client, id))

  describe("Create") {
    it("connector does not fail empty tasks list") {
      connector.saveData(PreviouslyCreatedTasksResolver.empty, List[GTask]().asJava, ProgressMonitorUtils.DUMMY_MONITOR, JiraFieldBuilder.getDefault())
    }

    it("tasks are created without errors") {
      CommonTestChecks.createsTasks(connector, JiraFieldBuilder.getDefault(), GTaskBuilder.getTwo(),
        id => TestJiraClientHelper.deleteTasks(client, id))
    }

    it("assignee and reporter are set") {
      val userPromise = client.getUserClient.getUser(setup.userName)
      val jiraUser = userPromise.claim
      val task = new GTask
      task.setValue(Summary, "some")
      val user = GUser(null, jiraUser.getName, jiraUser.getDisplayName)
      task.setValue(Assignee, user)
      task.setValue(Reporter, user)
      val loadedTask = TestUtils.saveAndLoad(connector, task, JiraFieldBuilder.getDefault())
      loadedTask.getValue(Assignee).loginName shouldBe jiraUser.getName
      loadedTask.getValue(Assignee).displayName shouldBe jiraUser.getDisplayName

      loadedTask.getValue(Reporter).loginName shouldBe jiraUser.getName
      loadedTask.getValue(Reporter).displayName shouldBe jiraUser.getDisplayName

      TestJiraClientHelper.deleteTasks(client, loadedTask.getIdentity)
    }

    it("status is set on create") {
      fixture.taskIsCreatedAndLoaded(
        GTaskBuilder.withSummary().setValue(TaskStatus, "In Progress"),
        Seq(Summary, TaskStatus))
    }
  }

  describe("Update") {
    it("fields are updated") {
      fixture.taskCreatedAndUpdatedOK(GTaskBuilder.withRandom(Summary),
        Seq((TaskStatus, "In Progress"),
          (Summary, "new value"),
          (Description, "new description")
        )
      )
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

  private def generateTasks: List[GTask] = {
    List(GTaskBuilder.withSummary().setId(1l).setKey("1"),
      GTaskBuilder.withSummary().setId(2l).setKey("2"))
  }
}
