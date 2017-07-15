package com.taskadapter.connector

import com.taskadapter.connector.it.{RedmineTestConfig, RedmineTestInitializer}
import com.taskadapter.connector.redmine.{CustomFieldBuilder, RedmineConnector, RedmineField}
import com.taskadapter.connector.testlib.InMemoryTaskKeeper
import com.taskadapter.redmineapi.bean.{Issue, IssueFactory, Project}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class NewIT extends FunSpec with Matchers with BeforeAndAfter with BeforeAndAfterAll {

  private var redmineProject: Option[Project] = None

  private val mgr = RedmineTestInitializer.mgr
  val sourceConfig = RedmineTestConfig.getRedmineTestConfig
  val targetConfig = RedmineTestConfig.getRedmineTestConfig
  val sourceConnector = new RedmineConnector(sourceConfig)
  val targetConnector = new RedmineConnector(targetConfig)
  val taskKeeper = new InMemoryTaskKeeper
  val adapter = new Adapter(taskKeeper, sourceConnector, targetConnector)

  before {
    // have to create a project for each test, otherwise stuff created during one test interferes with others
    redmineProject = Some(RedmineTestInitializer.createProject)
    sourceConfig.setProjectKey(redmineProject.get.getIdentifier)
    targetConfig.setProjectKey(redmineProject.get.getIdentifier)
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

