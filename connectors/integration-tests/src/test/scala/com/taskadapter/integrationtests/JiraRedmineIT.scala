package com.taskadapter.integrationtests

import com.taskadapter.connector.FieldRow
import com.taskadapter.connector.common.ProgressMonitorUtils
import com.taskadapter.connector.jira.{JiraConnector, JiraField}
import com.taskadapter.connector.redmine.{CustomFieldBuilder, RedmineConnector, RedmineField}
import com.taskadapter.connector.testlib.{TestSaver, TestUtils}
import com.taskadapter.core.TaskLoader
import com.taskadapter.model.{FieldRowBuilder, GTask, GUser}
import com.taskadapter.redmineapi.bean.{Issue, IssueFactory, Project}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSpec, Matchers}

import scala.collection.JavaConverters._

@RunWith(classOf[JUnitRunner])
class JiraRedmineIT extends FunSpec with Matchers with BeforeAndAfter with BeforeAndAfterAll {

  private var redmineProject: Option[Project] = None

  private val mgr = RedmineTestInitializer.mgr
  val redmineConfig = TestConfigs.getRedmineConfig
  val redmineConnector = new RedmineConnector(redmineConfig, TestConfigs.getRedmineServerInfo)
  val jiraConfig = TestConfigs.getJiraConfig
  val jiraSetup = TestConfigs.getJiraSetup
  val jiraConnector = new JiraConnector(jiraConfig, jiraSetup)
  val adapter = new Adapter(redmineConnector, jiraConnector)

  before {
    // have to create a project for each test, otherwise stuff created during one test interferes with others
    redmineProject = Some(RedmineTestInitializer.createProject)
    redmineConfig.setProjectKey(redmineProject.get.getIdentifier)
    redmineConfig.setFindUserByName(true)
  }

  after {
    RedmineTestInitializer.deleteProject(redmineProject.get.getIdentifier)
  }


  it("Description from Jira is saved to description in Redmine") {
    val rows = List(
      FieldRow(JiraField.summary, RedmineField.summary, ""),
      FieldRow(JiraField.description, RedmineField.description, "")
    )
    val task = new GTask()
    task.setValue(JiraField.summary, "summary1")
    task.setValue(JiraField.description, "description1")
    // create in Jira
    new TestSaver(redmineConnector, rows).saveAndLoad(task)

    // adapt
    val result = adapter.adapt(rows)

    // load from Redmine
    val redmine = TestUtils.loadCreatedTask(jiraConnector, rows.asJava, result)
    redmine.getValue(RedmineField.description) shouldBe "description1"
  }

  it("assignee and reporter can be loaded from JIRA and saved to Redmine") {
    val result = TestUtils.loadAndSave(jiraConnector, redmineConnector,
        Seq(RedmineField.summary, RedmineField.assignee, RedmineField.author)
    )
    val redmineAssignee = result.getValue(RedmineField.assignee).asInstanceOf[GUser]
    redmineAssignee.getDisplayName shouldBe "Redmine Admin"

    val redmineReporter = result.getValue(RedmineField.author).asInstanceOf[GUser]
    redmineReporter.getDisplayName shouldBe "Redmine Admin"
  }

  it("assignee can be loaded from Redmine and saved to JIRA") {
    val loadedTasks = TaskLoader.loadTasks(1, redmineConnector, "sourceName", ProgressMonitorUtils.DUMMY_MONITOR).asScala.toList

    val result = TestUtils.saveAndLoadList(jiraConnector, loadedTasks,
      FieldRowBuilder.rows(
        Seq(JiraField.summary, JiraField.assignee)
      )
    )
    val ass = result.head.getValue(JiraField.assignee).asInstanceOf[GUser]
    ass.getDisplayName shouldBe jiraSetup.userName

    val reporter = result.head.getValue(JiraField.reporter).asInstanceOf[GUser]
    reporter.getDisplayName shouldBe jiraSetup.userName
  }

  def createIssueInRedmineWithCustomField(fieldName: String, value: String): Issue = {
    val customFieldDefinitions = mgr.getCustomFieldManager.getCustomFieldDefinitions
    val issue = IssueFactory.create(redmineProject.get.getId, "some summary")
    CustomFieldBuilder.add(issue, customFieldDefinitions, fieldName, value)
    mgr.getIssueManager.createIssue(issue)
  }

  def createIssueInRedmine(description: String = ""): Issue = {
    val issue = IssueFactory.create(redmineProject.get.getId, "some summary")
    issue.setDescription(description)
    mgr.getIssueManager.createIssue(issue)
  }

}

