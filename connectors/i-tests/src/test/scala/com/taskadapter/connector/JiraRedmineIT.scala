package com.taskadapter.connector

import com.taskadapter.connector.it.RedmineTestInitializer
import com.taskadapter.connector.jira.{JiraConnector, JiraField, JiraPropertiesLoader}
import com.taskadapter.connector.redmine.{CustomFieldBuilder, RedmineConnector, RedmineField}
import com.taskadapter.connector.testlib.{TestSaver, TestUtils}
import com.taskadapter.model.GTask
import com.taskadapter.redmineapi.bean.{Issue, IssueFactory, Project}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSpec, Matchers}

import scala.collection.JavaConverters._

@RunWith(classOf[JUnitRunner])
class JiraRedmineIT extends FunSpec with Matchers with BeforeAndAfter with BeforeAndAfterAll {

  private var redmineProject: Option[Project] = None

  private val mgr = RedmineTestInitializer.mgr
  val sourceConfig = RedmineTestConfig.getRedmineTestConfig
  val targetConfig = JiraPropertiesLoader.createTestConfig
  val sourceConnector = new RedmineConnector(sourceConfig, RedmineTestConfig.getRedmineServerInfo)
  val targetConnector = new JiraConnector(targetConfig, JiraPropertiesLoader.getTestServerInfo)
  val adapter = new Adapter(sourceConnector, targetConnector)

  before {
    // have to create a project for each test, otherwise stuff created during one test interferes with others
    redmineProject = Some(RedmineTestInitializer.createProject)
    sourceConfig.setProjectKey(redmineProject.get.getIdentifier)
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
    new TestSaver(sourceConnector, rows.asJava).saveAndLoad(task)

    // adapt
    val result = adapter.adapt(rows)

    // load from Redmine
    val redmine = TestUtils.loadCreatedTask(targetConnector, rows.asJava, result)
    redmine.getValue(RedmineField.description) shouldBe "description1"
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

