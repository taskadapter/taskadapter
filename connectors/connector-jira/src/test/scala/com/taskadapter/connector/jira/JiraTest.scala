package com.taskadapter.connector.jira

import com.google.common.collect.Iterables
import com.taskadapter.connector.TestFieldBuilder
import com.taskadapter.connector.common.ProgressMonitorUtils
import com.taskadapter.connector.definition.TaskId
import com.taskadapter.connector.testlib.{CommonTestChecks, ITFixture, TestUtilsJava}
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

  private val logger = LoggerFactory.getLogger(classOf[JiraTestJava])

  private var config = JiraPropertiesLoader.createTestConfig
  private val setup = JiraPropertiesLoader.getTestServerInfo
  private var client = JiraConnectionFactory.createClient(setup)
  private var connector = new JiraConnector(config, setup)

  logger.info("Running JIRA tests using: " + setup.getHost)

  private val fixture = ITFixture(setup.getHost, connector, id => {
    TestJiraClientHelper.deleteTasks(client, id)
    null
  })

  describe("Create") {
    it("assignee and reporter are set") {
      val userPromise = client.getUserClient.getUser(setup.getUserName)
      val jiraUser = userPromise.claim
      val task = new GTask
      task.setValue(Summary, "some")
      task.setValue(AssigneeLoginName, jiraUser.getName)

      task.setValue(ReporterLoginName, jiraUser.getName)
      val loadedTask = TestUtilsJava.saveAndLoad(connector, task, TestFieldBuilder.getSummaryAndAssigneeLogin())
      loadedTask.getValue(AssigneeLoginName) shouldBe jiraUser.getName
      loadedTask.getValue(AssigneeFullName) shouldBe jiraUser.getDisplayName

      loadedTask.getValue(ReporterLoginName) shouldBe jiraUser.getName
      loadedTask.getValue(ReporterFullName) shouldBe jiraUser.getDisplayName

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
      fixture.taskCreatedAndUpdatedOK(GTaskBuilder.gtaskWithRandom(Summary),
        Seq((TaskStatus, "In Progress"),
          (Summary, "new value"),
          (Description, "new description")
        )
      )
    }
  }

  it("two issues linked") {
    config.setSaveIssueRelations(true)
    val list = generateTasks
    val task1 = list(0)
    val task2 = list(1)
    task1.getRelations.add(new GRelation(new TaskId(task1.getId, task1.getKey),
      new TaskId(task2.getId, task2.getKey), GRelationType.precedes))
    TestUtilsJava.saveAndLoadList(connector, list.asJava, TestFieldBuilder.getSummaryAndAssigneeLogin())
    val issues = TestJiraClientHelper.findIssuesBySummary(client, task1.getValue(Summary))
    val createdIssue1 = issues.iterator.next
    val links = createdIssue1.getIssueLinks
    assertEquals(1, Iterables.size(links))
    val link = links.iterator.next
    val targetIssueKey = link.getTargetIssueKey
    val createdIssue2 = TestJiraClientHelper.findIssuesBySummary(client, task2.getValue(Summary)).iterator.next
    assertEquals(createdIssue2.getKey, targetIssueKey)
    TestJiraClientHelper.deleteTasks(client, new TaskId(createdIssue1.getId, createdIssue1.getKey),
      new TaskId(createdIssue2.getId, createdIssue2.getKey))
  }

  private def generateTasks: List[GTask] = {
    List(GTaskBuilder.withSummary().setId(1L).setKey("1"),
      GTaskBuilder.withSummary().setId(2L).setKey("2"))
  }
}
