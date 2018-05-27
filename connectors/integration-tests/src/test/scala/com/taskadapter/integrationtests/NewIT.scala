package com.taskadapter.integrationtests

import java.io.File

import com.taskadapter.connector._
import com.taskadapter.connector.common.ProgressMonitorUtils
import com.taskadapter.connector.definition.FileSetup
import com.taskadapter.connector.jira.JiraConnector
import com.taskadapter.connector.msp.MSPConnector
import com.taskadapter.connector.redmine.{CustomFieldBuilder, RedmineConfig, RedmineConnector}
import com.taskadapter.connector.testlib._
import com.taskadapter.core.TaskLoader
import com.taskadapter.model._
import com.taskadapter.redmineapi.bean.{Issue, IssueFactory, Project}
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSpec, Matchers}

import scala.collection.JavaConverters._

@RunWith(classOf[JUnitRunner])
class NewIT extends FunSpec with Matchers with BeforeAndAfter with BeforeAndAfterAll with TempFolder {

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

  it("custom value saved to another custom value with default value") {
    val rows = List(
      FieldRow(Summary, Summary, ""),
      FieldRow(CustomString("my_custom_1"), CustomString("my_custom_2"), "default custom alex")
    )
    val issue = createIssueInRedmineWithCustomField(CustomString("my_custom_1"), "")
    val result = adapter.adapt(rows)

    val loaded = RedmineTestLoader.loadCreatedTask(mgr, result)
    loaded.getCustomFieldByName("my_custom_1").getValue shouldBe ""
    loaded.getCustomFieldByName("my_custom_2").getValue shouldBe "default custom alex"
  }

  it("Description field gets default value on save if needed") {
    val rows = List(
      FieldRow(Summary, Summary, ""),
      FieldRow(Description, Description, "default alex description")
    )
    val issue = createIssueInRedmine()
    val result = adapter.adapt(rows)

    val loaded = RedmineTestLoader.loadCreatedTask(mgr, result)
    loaded.getDescription shouldBe "default alex description"
  }

  it("Description field keeps source value when it is present") {
    val rows = List(
      FieldRow(Summary, Summary, ""),
      FieldRow(Description, Description, "default alex description")
    )
    val issue = createIssueInRedmine("description1")
    val result = adapter.adapt(rows)

    val loaded = RedmineTestLoader.loadCreatedTask(mgr, result)
    loaded.getDescription shouldBe "description1"

    mgr.getIssueManager.deleteIssue(issue.getId)
  }

  it("loads custom field in task and saves it to another custom field") {
    val rows = List(
      FieldRow(Summary, Summary, ""),
      FieldRow(Field("my_custom_1"), Field("my_custom_2"), "")
    )

    val issue = createIssueInRedmineWithCustomField(CustomString("my_custom_1"), "some value")
    val result = adapter.adapt(rows)

    val loaded = RedmineTestLoader.loadCreatedTask(mgr, result)
    loaded.getCustomFieldByName("my_custom_1").getValue shouldBe ""
    loaded.getCustomFieldByName("my_custom_2").getValue shouldBe "some value"
  }

  it("Description field value is saved to custom field") {
    val rows = List(
      FieldRow(Summary, Summary, ""),
      FieldRow(Description, Field("my_custom_1"), "")
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
      FieldRowBuilder.rows(
        Seq(Summary)
      )
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
      FieldRowBuilder.rows(Seq(Summary)
      )
    )
    assertEquals("must have created 13 tasks", 13, result.size)
  }

  describe("JIRA-Redmine") {

    it("Description from Jira is saved to description in Redmine") {
      val rows = List(
        FieldRow(Summary, Summary, ""),
        FieldRow(Description, Description, "")
      )
      val task = new GTask()
      task.setValue(Summary, "summary1")
      task.setValue(Description, "description1")
      new TestSaver(redmineConnectorWithResolveAssignees, rows).saveAndLoad(task)

      val result = adapter.adapt(rows)

      val redmine = TestUtils.loadCreatedTask(redmineConnectorWithResolveAssignees, rows, result)
      redmine.getValue(Description) shouldBe "description1"
    }

    it("assignee and reporter can be loaded from JIRA and saved to Redmine") {
      val rows = Seq(FieldRow(Summary, Summary, ""),
        FieldRow(AssigneeLoginName, AssigneeLoginName, null),
        FieldRow(ReporterLoginName, ReporterLoginName, null)
      )

      val fromJira = TestUtils.saveAndLoad(jiraConnector,
        new GTaskBuilder().withRandom(Summary).withAssigneeLogin(RedmineTestInitializer.currentUser.loginName).build(),
        rows)

      val redmineResult = TestUtils.saveAndLoad(redmineConnectorWithResolveAssignees, fromJira, rows)

      redmineResult.getValue(AssigneeFullName) shouldBe "Redmine Admin"
      redmineResult.getValue(AssigneeLoginName) shouldBe "user"

      redmineResult.getValue(ReporterFullName) shouldBe "Redmine Admin"
      redmineResult.getValue(ReporterLoginName) shouldBe "user"
    }

    it("assignee can be loaded from Redmine and saved to JIRA") {
      val created = createIssueInRedmine("some description", assignee = Some(RedmineTestInitializer.currentUser))
      val loadedTasks = TaskLoader.loadTasks(1, redmineConnectorWithResolveAssignees, "sourceName", ProgressMonitorUtils.DUMMY_MONITOR).asScala.toList
      loadedTasks.size shouldBe 1
      val redmineTask = loadedTasks.head
      redmineTask.getValue(AssigneeLoginName) shouldBe RedmineTestInitializer.currentUser.loginName

      val result = TestUtils.saveAndLoad(jiraConnector, redmineTask,
        Seq(
          FieldRow(Summary, Summary, ""),
          FieldRow(AssigneeLoginName, AssigneeLoginName, null)
        )
      )
      result.getValue(AssigneeLoginName) shouldBe jiraSetup.userName

      result.getValue(ReporterFullName) shouldBe jiraSetup.userName
    }

  }
  describe("Redmine-MSP") {
    it("assignee can be loaded from MSP and saved to Redmine") {
      val mspConnector = new MSPConnector(getMspSetup("2tasks-projectlibre-assignees.xml"))
      val redmineResult = TestUtils.loadAndSave(mspConnector, redmineConnectorWithResolveAssignees,
        Seq(FieldRow(Summary, Summary, ""),
          FieldRow(AssigneeFullName, AssigneeFullName, null)
        )
      )
      redmineResult.getValue(AssigneeFullName) shouldBe "Redmine Admin"
    }

    /**
      * This is a regression test for a bug reported by a user: Redmine subtasks were skipped when saving
      * to MSP: https://bitbucket.org/taskadapter/taskadapter/issues/85/subtasks-are-not-saved
      * Turned out all subtasks were broken in the system for a long time! X8-[==]
      */
    it("subtasks are saved to MSP") {
      withTempFolder { folder =>
        createRedmineHierarchy(targetRedmineConnector)
        val mspConnector = getMspConnector(folder)

        val result = TestUtils.loadAndSaveList(sourceRedmineConnector, mspConnector,
          Seq(FieldRow(Summary, Summary, "")
          )
        )
        result.size shouldBe 3
      }
    }
  }

  def createRedmineHierarchy(redmineConnector: RedmineConnector) = {
    val redmineFields = List(FieldRow(Summary, Summary, ""))

    val parent = GTaskBuilder.withSummary("parent task")
    val parentId = TestUtils.save(redmineConnector, parent, redmineFields)

    val sub1 = GTaskBuilder.withSummary("sub 1")
    sub1.setParentIdentity(parentId)

    val sub2 = GTaskBuilder.withSummary("sub 2")
    sub2.setParentIdentity(parentId)

    TestUtils.save(redmineConnector, sub1, redmineFields)
    TestUtils.save(redmineConnector, sub2, redmineFields)
  }

  def createIssueInRedmineWithCustomField(field: Field[_], value: String): Issue = {
    val customFieldDefinitions = mgr.getCustomFieldManager.getCustomFieldDefinitions
    val issue = IssueFactory.create(redmineProject.get.getId, "some summary")
    CustomFieldBuilder.add(issue, customFieldDefinitions, field, value)
    mgr.getIssueManager.createIssue(issue)
  }

  def createIssueInRedmine(description: String = "", assignee: Option[GUser] = None): Issue = {
    val issue = IssueFactory.create(redmineProject.get.getId, "some summary")
    issue.setDescription(description)
    if (assignee.isDefined) {
      issue.setAssigneeId(assignee.get.id)
    }
    mgr.getIssueManager.createIssue(issue)
  }

  def getMspSetup(resourceName: String): FileSetup = {
    val file = new File(ResourceLoader.getAbsolutePathForResource(resourceName))
    FileSetup(MSPConnector.ID, "label", file.getAbsolutePath, file.getAbsolutePath)
  }

  def getMspConnector(folder: File): MSPConnector = {
    val file = new File(folder, "msp_temp_file.xml")
    val setup = FileSetup(MSPConnector.ID, Some("file"), "label", file.getAbsolutePath, file.getAbsolutePath)
    new MSPConnector(setup)
  }
}

