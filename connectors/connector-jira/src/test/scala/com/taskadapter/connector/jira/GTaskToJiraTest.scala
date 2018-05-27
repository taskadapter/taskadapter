package com.taskadapter.connector.jira

import java.util.Calendar

import com.atlassian.jira.rest.client.api.domain.input.{ComplexIssueInputFieldValue, IssueInput}
import com.atlassian.jira.rest.client.api.domain.{IssueFieldId, IssueType, Priority}
import com.taskadapter.model._
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSpec, Matchers}
import scala.collection.JavaConverters._

@RunWith(classOf[JUnitRunner])
class GTaskToJiraTest extends FunSpec with Matchers with BeforeAndAfter with BeforeAndAfterAll {

  val config = JiraPropertiesLoader.createTestConfig
  var priorities = MockData.loadPriorities
  val issueTypeList = MockData.loadIssueTypes
  val versions = MockData.loadVersions
  val components = MockData.loadComponents

  it("priorityConvertedToCritical") {
    val priorityCritical = find(priorities, "Highest")
    val task = new JiraGTaskBuilder().withPriority(750).build()
    val converter = getConverter()
    val newIssue = converter.convertToJiraIssue(task).issueInput
    val actualPriorityId = getId(newIssue, IssueFieldId.PRIORITY_FIELD.id)
    assertEquals(priorityCritical.getId.toString, actualPriorityId)
  }

  it("issue type preserved if present"){
    val converter = getConverter
    val requiredIssueType = findIssueType(issueTypeList, "Bug")
    val task = JiraGTaskBuilder.withType("Bug")
    val issue = converter.convertToJiraIssue(task).issueInput
    assertEquals(requiredIssueType.getId.toString, getId(issue, IssueFieldId.ISSUE_TYPE_FIELD.id))
  }

  it("issue type set to default if not present"){
    val task = JiraGTaskBuilder.withType(null)
    val converter = getConverter
    val issue = converter.convertToJiraIssue(task).issueInput
    // must be default issue type if we set the field to null
    assertEquals(findDefaultIssueTypeId, getId(issue, IssueFieldId.ISSUE_TYPE_FIELD.id))
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

  it("dueDateConvertedWhenSelected"){
    checkDueDate(getConverter(), "2014-04-28")
  }

  private def checkDueDate(converter: GTaskToJira, expected: String): Unit = {
    val task = new GTask
    val calendar = Calendar.getInstance
    calendar.set(2014, Calendar.APRIL, 28, 0, 0, 0)
    task.setValue(DueDate, calendar.getTime)
    val issueInput = converter.convertToJiraIssue(task).issueInput
    assertEquals(expected, getValue(issueInput, IssueFieldId.DUE_DATE_FIELD.id))
  }

  it("reporter"){
    val task = new GTask().setValue(Reporter, new GUser(null, "mylogin", null))
    val issue = getConverter().convertToJiraIssue(task).issueInput
    assertEquals("mylogin", getComplexValue(issue, IssueFieldId.REPORTER_FIELD.id, "name"))
  }

  it("assigneeConvertedByDefault"){
    checkAssignee(getConverter(), "mylogin")
  }

  it("assigneeConvertedIfSelected"){
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

  private def findDefaultIssueTypeId = {
    val defaultTaskTypeName = config.getDefaultTaskType
    val issueType = findIssueType(issueTypeList, defaultTaskTypeName)
    issueType.getId.toString
  }

  private def findIssueType(issueTypes: Iterable[IssueType], `type`: String): IssueType = {
    for (issueType <- issueTypes) {
      if (issueType.getName == `type`) return issueType
    }
    throw new RuntimeException("Not found: " + `type`)
  }

  val customFieldsResolver = new CustomFieldResolver(Seq())
  private def getConverter(): GTaskToJira = new GTaskToJira(config, customFieldsResolver, issueTypeList, versions, components, priorities)

  private def find(priorities: Iterable[Priority], priorityName: String): Priority = {
    for (priority <- priorities) {
      if (priority.getName == priorityName) return priority
    }
    throw new RuntimeException("Priority not found: " + priorityName)
  }
}
