package com.taskadapter.connector.jira

import com.atlassian.jira.rest.client.api.domain.Issue
import com.taskadapter.connector.Priorities
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSpec, Matchers}

//@RunWith(classOf[JUnitRunner])
class JiraToGTaskTest extends FunSpec with Matchers with BeforeAndAfter with BeforeAndAfterAll {

  private var priorities = JiraConfig.createDefaultPriorities

  it("summaryIsConverted") {
    val issue = MockData.loadIssue("issue_jira_5.0.1.json")
    val task = convertIssue(issue)
    task.getValue(JiraField.summary) shouldBe issue.getSummary
  }

  it("descriptionIsConverted") {
    val issue = MockData.loadIssue("issue_jira_5.0.1.json")
    val task = convertIssue(issue)
    task.getValue(JiraField.description) shouldBe issue.getDescription
  }

  it("estimatedTimeConvertedByDefault") {
    val issue = MockData.loadIssue("issue_with_time_tracking_5.0.json")
    val task = convertIssue(issue)
    task.getValue(JiraField.estimatedTime) shouldBe 45.5f
  }

  it("assigneeIsConverted") { // TODO cannot parse an issue without "names" and "schema" section. submitted a bug:
    // https://answers.atlassian.com/questions/32971227/jira-java-rest-client-cannot-parse-a-valid-issue-json-returned-by-jira-6.4.11-npe-at-jsonparseutil.getstringkeysjsonparseutil.java337
    val issue = MockData.loadIssue("issue_with_assignee_6.4.11_expanded_names_and_schema.json")
    val task = convertIssue(issue)
    task.getValue(JiraField.assignee) shouldBe issue.getAssignee.getName
  }

  it("issueTypeIsConverted") {
    val issue = MockData.loadIssue("issue_jira_5.0.1.json")
    val task = convertIssue(issue)
    task.getValue(JiraField.taskType) shouldBe issue.getIssueType.getName
  }

  it("dueDateNullValueIsConverted") {
    val issue = MockData.loadIssue("issue_jira_5.0.1.json")
    val task = convertIssue(issue)
    task.getValue(JiraField.dueDate) shouldBe null
  }

  it("dueDateIsConverted") {
    val issue = MockData.loadIssue("issue_jira_duedate_5.0.1.json")
    val task = convertIssue(issue)
    task.getValue(JiraField.dueDate) shouldBe issue.getDueDate.toDate
  }

  it("setDefaultPriorityWhenIsNull") {
    val issue = MockData.loadIssue("issue_jira_5.0.1.json")
    val task = convertIssue(issue)
    task.getValue(JiraField.priority) shouldBe Priorities.DEFAULT_PRIORITY_VALUE
  }

  private def convertIssue(issue: Issue) = {
    val jiraToGTask = new JiraToGTask(priorities)
    jiraToGTask.convertToGenericTask(issue)
  }
}
