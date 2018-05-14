package com.taskadapter.connector.jira

import com.atlassian.jira.rest.client.api.domain.Issue
import com.taskadapter.connector.Priorities
import com.taskadapter.model._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSpec, Matchers}
import scala.collection.JavaConverters._

@RunWith(classOf[JUnitRunner])
class JiraToGTaskTest extends FunSpec with Matchers with BeforeAndAfter with BeforeAndAfterAll {

  private var priorities = JiraConfig.createDefaultPriorities

  it("summaryIsConverted") {
    val issue = MockData.loadIssue("issue_jira_5.0.1.json")
    val task = convertIssue(issue)
    task.getValue(Summary) shouldBe issue.getSummary
  }

  it("descriptionIsConverted") {
    val issue = MockData.loadIssue("issue_jira_5.0.1.json")
    val task = convertIssue(issue)
    task.getValue(Description) shouldBe issue.getDescription
  }

  it("status") {
    val issue = MockData.loadIssue("issue_jira_5.0.1.json")
    convertIssue(issue).getValue(TaskStatus) shouldBe issue.getStatus.getName
  }

  it("estimatedTimeConvertedByDefault") {
    val issue = MockData.loadIssue("issue_with_time_tracking_5.0.json")
    val task = convertIssue(issue)
    task.getValue(EstimatedTime) shouldBe 45.5f
  }

  it("assigneeIsConverted") { // TODO cannot parse an issue without "names" and "schema" section. submitted a bug:
    // https://answers.atlassian.com/questions/32971227/jira-java-rest-client-cannot-parse-a-valid-issue-json-returned-by-jira-6.4.11-npe-at-jsonparseutil.getstringkeysjsonparseutil.java337
    val issue = MockData.loadIssue("issue_with_assignee_6.4.11_expanded_names_and_schema.json")
    val task = convertIssue(issue)
    task.getValue(Assignee).getLoginName shouldBe issue.getAssignee.getName
  }

  it("reporter") { // TODO cannot parse an issue without "names" and "schema" section. submitted a bug:
    // https://answers.atlassian.com/questions/32971227/jira-java-rest-client-cannot-parse-a-valid-issue-json-returned-by-jira-6.4.11-npe-at-jsonparseutil.getstringkeysjsonparseutil.java337
    val issue = MockData.loadIssue("issue_with_assignee_6.4.11_expanded_names_and_schema.json")
    convertIssue(issue).getValue(Reporter).getLoginName shouldBe issue.getReporter.getName
  }

  it("issue type") {
    val issue = MockData.loadIssue("issue_jira_5.0.1.json")
    val task = convertIssue(issue)
    task.getValue(TaskType) shouldBe issue.getIssueType.getName
  }

  it("dueDateNullValueIsConverted") {
    val issue = MockData.loadIssue("issue_jira_5.0.1.json")
    val task = convertIssue(issue)
    task.getValue(DueDate) shouldBe null
  }

  it("dueDateIsConverted") {
    val issue = MockData.loadIssue("issue_jira_duedate_5.0.1.json")
    val task = convertIssue(issue)
    task.getValue(DueDate) shouldBe issue.getDueDate.toDate
  }

  it("created on") {
    val issue = MockData.loadIssue("issue_jira_duedate_5.0.1.json")
    val task = convertIssue(issue)
    task.getValue(CreatedOn) shouldBe issue.getCreationDate.toDate
  }

  it("no components gives empty list") {
    val issue = MockData.loadIssue("issue_jira_duedate_5.0.1.json")
    convertIssue(issue).getValue(Components) shouldBe issue.getComponents.asScala.map(_.getName)
  }

  it("components") {
    val issue = MockData.loadIssue("issue_with_components_jira_7.1.9.json")
    convertIssue(issue).getValue(Components) shouldBe issue.getComponents.asScala.map(_.getName)
  }

  it("setDefaultPriorityWhenIsNull") {
    val issue = MockData.loadIssue("issue_jira_5.0.1.json")
    val task = convertIssue(issue)
    task.getValue(Priority) shouldBe Priorities.DEFAULT_PRIORITY_VALUE
  }

  it("custom fields") {
    val issue = MockData.loadIssue("7.1.9/issue_with_custom_options_checkboxes_jira_7.1.9.json")
    val jiraToGTask = new JiraToGTask(priorities)
    val fields = MockData.loadFieldDefinitions
    val task = jiraToGTask.convertToGenericTask(new CustomFieldResolver(fields), issue)

    task.getValue(CustomSeqString("custom_checkbox_1")) should contain only ("option1", "option2")
  }

  private def convertIssue(issue: Issue) = {
    val jiraToGTask = new JiraToGTask(priorities)
    jiraToGTask.convertToGenericTask(new CustomFieldResolver(Seq()), issue)
  }
}
