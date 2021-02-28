package com.taskadapter.connector.jira

import com.atlassian.jira.rest.client.api.domain.input.{ComplexIssueInputFieldValue, IssueInput}
import com.atlassian.jira.rest.client.api.domain.{IssueFieldId, Priority}
import com.taskadapter.connector.definition.exception.FieldConversionException
import com.taskadapter.model._
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSpec, Matchers}

import java.util.Calendar
import scala.collection.JavaConverters._

@RunWith(classOf[JUnitRunner])
class GTaskToJiraTest extends FunSpec with Matchers with BeforeAndAfter with BeforeAndAfterAll {

  it("priorityConvertedToCritical") {
    val priorityCritical = find(GTaskToJiraFactory.defaultPriorities, "Highest")
    val task = JiraGTaskBuilder.builderWithSummary().withPriority(750).build()
    val converter = getConverter()
    val newIssue = converter.convertToJiraIssue(task).issueInput
    val actualPriorityId = getId(newIssue, IssueFieldId.PRIORITY_FIELD.id)
    assertEquals(priorityCritical.getId.toString, actualPriorityId)
  }

  /**
    * regression test: priority conversion was failing with exception when priorities loaded from server did not contain
    * priority name set in JIRA config. e.g. if JIRA is set to have non-english priority names,
    * while TaskAdapter JIRA config has english names in its "priority mapping" table.
    */
  it("unknown priority name gives user-friendly error") {
    val task = JiraGTaskBuilder.builderWithSummary().withPriority(500).build()
    val converter = GTaskToJiraFactory.getConverter(priorities = Seq())
    val exception = intercept[FieldConversionException] {
      converter.convertToJiraIssue(task).issueInput
    }
    exception.getMessage should include ("Reason: Priority with name Medium is not found on the server")
  }

  it("summary is converted"){
    checkSummary(getConverter(), "summary here")
  }

  private def checkSummary(converter: GTaskToJira, expectedValue: String): Unit = {
    val task = GTaskBuilder.withSummary(expectedValue)
    val issueInput = converter.convertToJiraIssue(task).issueInput
    assertEquals(expectedValue, getValue(issueInput, IssueFieldId.SUMMARY_FIELD.id))
  }

  it("description"){
    checkDescription(getConverter(), "description here")
  }

  it("status"){
    val task = new GTask().setValue(TaskStatus, "TO DO")
    getConverter().convertToJiraIssue(task).status shouldBe "TO DO"
  }

  private def checkDescription(converter: GTaskToJira, expectedValue: String): Unit = {
    val task = new GTask().setValue(Description, expectedValue)

    val issueInput = converter.convertToJiraIssue(task).issueInput
    assertEquals(expectedValue, getValue(issueInput, IssueFieldId.DESCRIPTION_FIELD.id))
  }

  it("dueDateConvertedByDefault"){
    checkDueDate(getConverter, "2014-04-28")
  }

  private def checkDueDate(converter: GTaskToJira, expected: String): Unit = {
    val task = new GTask
    val calendar = Calendar.getInstance
    calendar.set(2014, Calendar.APRIL, 28, 0, 0, 0)
    task.setValue(DueDate, calendar.getTime)
    val issueInput = converter.convertToJiraIssue(task).issueInput
    assertEquals(expected, getValue(issueInput, IssueFieldId.DUE_DATE_FIELD.id))
  }

  it("reporter login name"){
    val task = new GTask().setValue(ReporterLoginName, "mylogin")
    val issue = getConverter().convertToJiraIssue(task).issueInput
    assertEquals("mylogin", getComplexValue(issue, IssueFieldId.REPORTER_FIELD.id, "name"))
  }

  it("assigneeConvertedByDefault"){
    checkAssignee(getConverter(), "mylogin")
  }

  private def checkAssignee(converter: GTaskToJira, expected: String): Unit = {
    val task = new GTask().setValue(AssigneeLoginName, expected)
    val issue = converter.convertToJiraIssue(task).issueInput
    assertEquals(expected, getComplexValue(issue, IssueFieldId.ASSIGNEE_FIELD.id, "name"))
  }

  it ("components use only the first provided value and ignore others") {
    val task = new GTask().setValue(Components, Seq("client", "server"))
    val issue = getConverter().convertToJiraIssue(task).issueInput
    getIterableValue(issue, IssueFieldId.COMPONENTS_FIELD.id) should contain only("client")
  }

  it ("empty component is valid") {
    val task = new GTask().setValue(Components, Seq())
    val issue = getConverter().convertToJiraIssue(task).issueInput
    getIterableValue(issue, IssueFieldId.COMPONENTS_FIELD.id) shouldBe null
  }

  it("estimated time"){
    checkEstimatedTime(getConverter(), "180m")
  }

  private def checkEstimatedTime(converter: GTaskToJira, expectedTime: String): Unit = {
    val task = new GTask
    task.setValue(EstimatedTime, 3f)
    val issue = converter.convertToJiraIssue(task).issueInput
    assertEquals(expectedTime, getComplexValue(issue, "timetracking", "originalEstimate"))
  }

  private def getId(issue: IssueInput, fieldName: String) = {
    val field = issue.getField(fieldName)
    val value = field.getValue.asInstanceOf[ComplexIssueInputFieldValue]
    value.getValuesMap.get("id").asInstanceOf[String]
  }

  private def getValue(issue: IssueInput, fieldName: String): String = {
    val field = issue.getField(fieldName)
    if (field == null) return null
    field.getValue.asInstanceOf[String]
  }

  private def getComplexValue(issue: IssueInput, fieldName: String, subFieldName: String): String = {
    val field = issue.getField(fieldName)
    if (field == null) return null
    val value = field.getValue.asInstanceOf[ComplexIssueInputFieldValue]
    value.getValuesMap.get(subFieldName).asInstanceOf[String]
  }

  private def getIterableValue(issue: IssueInput, fieldName: String): Seq[String] = {
    val field = issue.getField(fieldName)
    if (field == null) return null
    val value = field.getValue.asInstanceOf[java.lang.Iterable[ComplexIssueInputFieldValue]]
    value.asScala
      .map(v => v.getValuesMap().get("name").asInstanceOf[String]).toSeq
  }

  private def getConverter(): GTaskToJira = GTaskToJiraFactory.getConverter()

  private def find(priorities: Iterable[Priority], priorityName: String): Priority = {
    for (priority <- priorities) {
      if (priority.getName == priorityName) return priority
    }
    throw new RuntimeException("Priority not found: " + priorityName)
  }
}
