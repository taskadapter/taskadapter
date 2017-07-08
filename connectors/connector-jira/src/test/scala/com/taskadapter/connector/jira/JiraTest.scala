package com.taskadapter.connector.jira

import java.util

import com.google.common.collect.Iterables
import com.taskadapter.connector.common.ProgressMonitorUtils
import com.taskadapter.connector.testlib.TestUtils
import com.taskadapter.model.{GRelation, GTask}
import org.fest.assertions.Assertions.assertThat
import org.junit.Assert.assertEquals
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSpec, Matchers}
import org.slf4j.LoggerFactory

class JiraTest extends FunSpec with Matchers with BeforeAndAfter with BeforeAndAfterAll {

  private val logger = LoggerFactory.getLogger(classOf[JiraTest])

  private var config = JiraPropertiesLoader.createTestConfig
  private var client = JiraConnectionFactory.createClient(config.getServerInfo)
  private var connector = new JiraConnector(config)

  logger.info("Running JIRA tests using: " + config.getServerInfo.getHost)


  it("doesNotFailWithNULLMonitorAndEmptyList") {
    connector.saveData(new util.ArrayList[GTask], ProgressMonitorUtils.DUMMY_MONITOR, JiraFieldBuilder.getDefault)
  }

  /*
      @Test
      public void twoTasksAreCreated() throws Exception {
          CommonTests.testCreates2Tasks(connector, JiraFieldBuilder.getDefault());
      }
  */

  it("assigneeHasFullName") {
    val userPromise = client.getUserClient.getUser(config.getServerInfo.getUserName)
    val jiraUser = userPromise.claim
    val task = new GTask
    task.setValue(JiraField.summary, "some")
    task.setValue(JiraField.assignee, jiraUser.getName)
    val loadedTask = TestUtils.saveAndLoad(connector, task, JiraFieldBuilder.getDefault)
    assertEquals(jiraUser.getName, loadedTask.getValue(JiraField.assignee))
    TestJiraClientHelper.deleteTasks(client, loadedTask.getKey)
  }

  /*
      @Test
      public void taskUpdatedOK() throws Exception {
          CommonTests.taskCreatedAndUpdatedOK(connector, SUPPORTED_FIELDS);
      }
  */

  it("testGetIssuesByProject") {
    val tasks = generateTasks
    connector.saveData(tasks, ProgressMonitorUtils.DUMMY_MONITOR, JiraFieldBuilder.getDefault)
    val jql = JqlBuilder.findIssuesByProject(config.getProjectKey)
    val issues = JiraClientHelper.findIssues(client, jql)
    assertThat(Iterables.size(issues)).isGreaterThan(1)
  }

  it("twoIssuesLinked") {
    config.setSaveIssueRelations(true)
    val list = generateTasks
    val task1 = list.get(0)
    val task2 = list.get(1)
    task1.getRelations.add(new GRelation(task1.getId.toString, task2.getId.toString, GRelation.TYPE.precedes))
    TestUtils.saveAndLoadList(connector, list, JiraFieldBuilder.getDefault)
    val issues = TestJiraClientHelper.findIssuesBySummary(client, task1.getValue(JiraField.summary).asInstanceOf[String])
    val createdIssue1 = issues.iterator.next
    val links = createdIssue1.getIssueLinks
    assertEquals(1, Iterables.size(links))
    val link = links.iterator.next
    val targetIssueKey = link.getTargetIssueKey
    val createdIssue2 = TestJiraClientHelper.findIssuesBySummary(client, task2.getValue(JiraField.summary).asInstanceOf[String]).iterator.next
    assertEquals(createdIssue2.getKey, targetIssueKey)
    TestJiraClientHelper.deleteTasks(client, createdIssue1.getKey, createdIssue2.getKey)
  }

  private def generateTasks = {
    val task1 = JiraGTaskBuilder.withSummary
    task1.setId(1)
    val task2 = JiraGTaskBuilder.withSummary
    task2.setId(2)
    val list = new util.ArrayList[GTask]
    list.add(task1)
    list.add(task2)
    list
  }
}
