package com.taskadapter.integrationtests

import java.io.File

import com.taskadapter.connector._
import com.taskadapter.connector.common.ProgressMonitorUtils
import com.taskadapter.connector.definition.FileSetup
import com.taskadapter.connector.jira.{JiraConnector, JiraField}
import com.taskadapter.connector.msp.{MSPConnector, MspField}
import com.taskadapter.connector.redmine._
import com.taskadapter.connector.testlib.{ResourceLoader, TestSaver, TestUtils}
import com.taskadapter.core.TaskLoader
import com.taskadapter.model.{FieldRowBuilder, GTask, GUser}
import com.taskadapter.redmineapi.bean.{Issue, IssueFactory, Project}
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSpec, Matchers}

import scala.collection.JavaConverters._

@RunWith(classOf[JUnitRunner])
class NewIT extends FunSpec with Matchers with BeforeAndAfter with BeforeAndAfterAll {

  private var redmineProject: Option[Project] = None

  private val mgr = RedmineTestInitializer.mgr

  val sourceConfig = TestConfigs.getRedmineConfig
  val targetConfig = TestConfigs.getRedmineConfig
  val sourceRedmineConnector = new RedmineConnector(sourceConfig, TestConfigs.getRedmineSetup)
  val targetRedmineConnector = new RedmineConnector(targetConfig, TestConfigs.getRedmineSetup)

  val jiraConfig = TestConfigs.getJiraConfig
  val jiraSetup = TestConfigs.getJiraSetup
  val jiraConnector = new JiraConnector(jiraConfig, jiraSetup)

  val redmineConfigWithResolveAssignees = TestConfigs.getRedmineConfig
  redmineConfigWithResolveAssignees.setFindUserByName(true)
  val redmineConnectorWithResolveAssignees = new RedmineConnector(redmineConfigWithResolveAssignees, TestConfigs.getRedmineSetup)

  val adapter = new Adapter(sourceRedmineConnector, targetRedmineConnector)

  before {
    // have to create a project for each test, otherwise stuff created during one test interferes with others
    redmineProject = Some(RedmineTestInitializer.createProject)
    sourceConfig.setProjectKey(redmineProject.get.getIdentifier)
    targetConfig.setProjectKey(redmineProject.get.getIdentifier)
    redmineConfigWithResolveAssignees.setProjectKey(redmineProject.get.getIdentifier)
  }

  after {
    RedmineTestInitializer.deleteProject(redmineProject.get.getIdentifier)
  }

  /*
    FieldRow(true, "done_ratio", "done_ratio", ""),
    FieldRow(true, "due_date", "due_date", ""),
    FieldRow(true, "assignee", "assignee", ""),
  */

  it("custom value saved to another custom value with default value") {
    val rows = List(
      FieldRow(RedmineField.summary, RedmineField.summary, ""),
      FieldRow(Field("my_custom_1"), Field("my_custom_2"), "default custom alex")
    )
    val issue = createIssueInRedmineWithCustomField("my_custom_1", "")
    val result = adapter.adapt(rows)

    val loaded = RedmineTestLoader.loadCreatedTask(mgr, result)
    loaded.getCustomFieldByName("my_custom_1").getValue shouldBe ""
    loaded.getCustomFieldByName("my_custom_2").getValue shouldBe "default custom alex"
  }

  it("Description field gets default value on save if needed") {
    val rows = List(
      FieldRow(RedmineField.summary, RedmineField.summary, ""),
      FieldRow(RedmineField.description, RedmineField.description, "default alex description")
    )
    val issue = createIssueInRedmine()
    val result = adapter.adapt(rows)

    val loaded = RedmineTestLoader.loadCreatedTask(mgr, result)
    loaded.getDescription shouldBe "default alex description"
  }

  it("Description field keeps source value when it is present") {
    val rows = List(
      FieldRow(RedmineField.summary, RedmineField.summary, ""),
      FieldRow(RedmineField.description, RedmineField.description, "default alex description")
    )
    val issue = createIssueInRedmine("description1")
    val result = adapter.adapt(rows)

    val loaded = RedmineTestLoader.loadCreatedTask(mgr, result)
    loaded.getDescription shouldBe "description1"

    mgr.getIssueManager.deleteIssue(issue.getId)
  }

  it("loads custom field in task and saves it to another custom field") {
    val rows = List(
      FieldRow(RedmineField.summary, RedmineField.summary, ""),
      FieldRow(Field("my_custom_1"), Field("my_custom_2"), "")
    )

    val issue = createIssueInRedmineWithCustomField("my_custom_1", "some value")
    val result = adapter.adapt(rows)

    val loaded = RedmineTestLoader.loadCreatedTask(mgr, result)
    loaded.getCustomFieldByName("my_custom_1").getValue shouldBe ""
    loaded.getCustomFieldByName("my_custom_2").getValue shouldBe "some value"
  }

  it("Description field value is saved to custom field") {
    val rows = List(
      FieldRow(RedmineField.summary, RedmineField.summary, ""),
      FieldRow(RedmineField.description, Field("my_custom_1"), "")
    )
    val issue = createIssueInRedmine("description 1")
    val result = adapter.adapt(rows)

    val loaded = RedmineTestLoader.loadCreatedTask(mgr, result)
    loaded.getCustomFieldByName("my_custom_1").getValue shouldBe "description 1"
  }

  it("msp tasks with non-linear IDs are saved to Redmine") {
    val msProjectConnector = new MSPConnector(getMspSetup("com/taskadapter/integrationtests/non-linear-uuid.xml"))
    val redmineConfig: RedmineConfig = TestConfigs.getRedmineConfig
    redmineConfig.setProjectKey(redmineProject.get.getIdentifier)

    // load from MSP
    val maxTasksNumber = 9999
    val loadedTasks = TaskLoader.loadTasks(maxTasksNumber, msProjectConnector, "msp1",
      ProgressMonitorUtils.DUMMY_MONITOR).asScala.toList

    val redmineConnector = new RedmineConnector(redmineConfig, TestConfigs.getRedmineSetup)
    // save to Redmine
    val result = TestUtils.saveAndLoadList(redmineConnector, loadedTasks,
      FieldRowBuilder.rows(Seq(
        RedmineField.summary
      ))
    )
    assertEquals("must have created 2 tasks", 2, result.size)
  }

  it("msp tasks with one-side disconnected relationships are saved to Redmine") {
    val redmineConfig = TestConfigs.getRedmineConfig
    redmineConfig.setProjectKey(redmineProject.get.getIdentifier)

    val projectConnector = new MSPConnector(
      getMspSetup("com/taskadapter/integrationtests/ProjectWithOneSideDisconnectedRelationships.xml"))

    val maxTasksNumber = 9999
    val loadedTasks = TaskLoader.loadTasks(maxTasksNumber, projectConnector, "project1",
      ProgressMonitorUtils.DUMMY_MONITOR).asScala.toList
    // save to Redmine
    val redmineConnector = new RedmineConnector(redmineConfig, TestConfigs.getRedmineSetup)

    val result = TestUtils.saveAndLoadList(redmineConnector, loadedTasks,
      FieldRowBuilder.rows(Seq(
        RedmineField.summary
      ))
    )
    assertEquals("must have created 13 tasks", 13, result.size)
  }

  describe("JIRA-Redmine") {

    it("Description from Jira is saved to description in Redmine") {
      val rows = List(
        FieldRow(JiraField.summary, RedmineField.summary, ""),
        FieldRow(JiraField.description, RedmineField.description, "")
      )
      val task = new GTask()
      task.setValue(JiraField.summary, "summary1")
      task.setValue(JiraField.description, "description1")
      new TestSaver(redmineConnectorWithResolveAssignees, rows).saveAndLoad(task)

      val result = adapter.adapt(rows)

      val redmine = TestUtils.loadCreatedTask(redmineConnectorWithResolveAssignees, rows.asJava, result)
      redmine.getValue(RedmineField.description) shouldBe "description1"
    }

    it("assignee and reporter can be loaded from JIRA and saved to Redmine") {
      val result = TestUtils.loadAndSave(jiraConnector, redmineConnectorWithResolveAssignees,
        Seq(FieldRow(JiraField.summary, RedmineField.summary, ""),
          FieldRow(JiraField.assignee, RedmineField.assignee, ""),
          FieldRow(JiraField.reporter, RedmineField.author, "")
        ))
      val redmineAssignee = result.getValue(RedmineField.assignee).asInstanceOf[GUser]
      redmineAssignee.getDisplayName shouldBe "Redmine Admin"

      val redmineReporter = result.getValue(RedmineField.author).asInstanceOf[GUser]
      redmineReporter.getDisplayName shouldBe "Redmine Admin"
    }

    it("assignee can be loaded from Redmine and saved to JIRA") {
      val created = createIssueInRedmine("some description", assignee = Some(RedmineTestInitializer.currentUser))
      val loadedTasks = TaskLoader.loadTasks(1, redmineConnectorWithResolveAssignees, "sourceName", ProgressMonitorUtils.DUMMY_MONITOR).asScala.toList
      loadedTasks.size shouldBe 1
      val redmineTask = loadedTasks.head
      redmineTask.getValue(RedmineField.assignee).asInstanceOf[GUser].getLoginName shouldBe RedmineTestInitializer.currentUser.getLoginName

      val result = TestUtils.saveAndLoad(jiraConnector, redmineTask,
        Seq(
          FieldRow(RedmineField.summary, JiraField.summary, ""),
          FieldRow(RedmineField.assignee, JiraField.assignee, "")
        )
      )
      val ass = result.getValue(JiraField.assignee).asInstanceOf[GUser]
      ass.getDisplayName shouldBe jiraSetup.userName

      val reporter = result.getValue(JiraField.reporter).asInstanceOf[GUser]
      reporter.getDisplayName shouldBe jiraSetup.userName
    }

  }
  describe("Redmine-MSP") {
    it("assignee can be loaded from MSP and saved to Redmine") {
      val mspConnector = new MSPConnector(getMspSetup("2tasks-projectlibre-assignees.xml"))
      val result = TestUtils.loadAndSave(mspConnector, redmineConnectorWithResolveAssignees,
        Seq(FieldRow(MspField.summary, MspField.summary, ""),
          FieldRow(MspField.assignee, RedmineField.assignee, "")
        )
      )
      val ass = result.getValue(RedmineField.assignee).asInstanceOf[GUser]
      ass.getDisplayName shouldBe "Redmine Admin"
    }
  }

  def createIssueInRedmineWithCustomField(fieldName: String, value: String): Issue = {
    val customFieldDefinitions = mgr.getCustomFieldManager.getCustomFieldDefinitions
    val issue = IssueFactory.create(redmineProject.get.getId, "some summary")
    CustomFieldBuilder.add(issue, customFieldDefinitions, fieldName, value)
    mgr.getIssueManager.createIssue(issue)
  }

  def createIssueInRedmine(description: String = "", assignee: Option[GUser] = None): Issue = {
    val issue = IssueFactory.create(redmineProject.get.getId, "some summary")
    issue.setDescription(description)
    if (assignee.isDefined) {
      issue.setAssigneeId(assignee.get.getId)
    }
    mgr.getIssueManager.createIssue(issue)
  }

  def getMspSetup(resourceName: String): FileSetup = {
    val file = new File(ResourceLoader.getAbsolutePathForResource(resourceName))
    FileSetup(MSPConnector.ID, "label", file.getAbsolutePath, file.getAbsolutePath)
  }
}

