package com.taskadapter.connector.redmine

import com.taskadapter.connector.Priorities
import com.taskadapter.connector.definition.TaskId
import com.taskadapter.model.{GRelationType, _}
import com.taskadapter.redmineapi.bean._
import org.junit.Assert.{assertEquals, assertNull}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSpec, Matchers}

import java.util.{Calendar, Collections}

@RunWith(classOf[JUnitRunner])
class RedmineToGTaskTest extends FunSpec with Matchers with BeforeAndAfter with BeforeAndAfterAll {

  val userCache = new RedmineUserCache(Seq())

  private def get(): RedmineToGTask = {
    val config = new RedmineConfig
    new RedmineToGTask(config, userCache)
  }

  it("summaryIsConverted") {
    val redmineIssue = new Issue
    redmineIssue.setSubject("text 1")
    val task = get().convertToGenericTask(redmineIssue)
    assertEquals("text 1", task.getValue(AllFields.summary))
  }

  it("descriptionIsConverted") {
    val redmineIssue = new Issue
    redmineIssue.setDescription("description 1")
    val task = get().convertToGenericTask(redmineIssue)
    assertEquals("description 1", task.getValue(AllFields.description))
  }

  it("idIsConvertedIfSet") {
    val redmineIssue = IssueFactory.create(123)
    val task = get().convertToGenericTask(redmineIssue)
    task.getId shouldBe 123
  }

  it("idIsIgnoredIfNull") {
    val redmineIssue = new Issue
    val task = get().convertToGenericTask(redmineIssue)
    assertNull(task.getId)
  }

  it("idIsSetToKey") {
    val redmineIssue = IssueFactory.create(123)
    val task = get().convertToGenericTask(redmineIssue)
    assertEquals("123", task.getKey)
  }

  it("parentIdIsConvertedIfSet") {
    val redmineIssue = new Issue
    redmineIssue.setParentId(123)
    val task = get().convertToGenericTask(redmineIssue)
    task.getParentIdentity shouldBe new TaskId(123, "123")
  }

  it("parentIdIsIgnoredIfNotSet") {
    val redmineIssue = new Issue
    val task = get().convertToGenericTask(redmineIssue)
    assertNull(task.getParentIdentity)
  }

  it("assigneeIsIgnoredIfNotSet") {
    val redmineIssue = new Issue
    val task = get().convertToGenericTask(redmineIssue)
    assertNull(task.getValue(AllFields.assigneeFullName))
    assertNull(task.getValue(AllFields.assigneeLoginName))
  }

  it("trackerTypeIsConvertedIfSet") {
    val redmineIssue = new Issue
    val tracker = TrackerFactory.create(123, "something")
    redmineIssue.setTracker(tracker)
    val task = get().convertToGenericTask(redmineIssue)
    assertEquals("something", task.getValue(AllFields.taskType))
  }

  it("trackerTypeIsIgnoredIfNotSet") {
    val redmineIssue = new Issue
    val task = get().convertToGenericTask(redmineIssue)
    assertNull(task.getValue(AllFields.taskType))
  }

  it("statusIsConverted") {
    val redmineIssue = new Issue
    redmineIssue.setStatusName("some status")
    val task = get().convertToGenericTask(redmineIssue)
    assertEquals("some status", task.getValue(AllFields.taskStatus))
  }

  it("estimatedHoursAreConverted") {
    val redmineIssue = new Issue
    redmineIssue.setEstimatedHours(55f)
    val task = get().convertToGenericTask(redmineIssue)
    task.getValue(AllFields.estimatedTime) shouldBe 55f
  }

  it("doneRatioIsConverted") {
    val redmineIssue = new Issue
    redmineIssue.setDoneRatio(75)
    val task = get().convertToGenericTask(redmineIssue)
    task.getValue(AllFields.doneRatio) shouldBe 75f
  }

  it("startDateIsConverted") {
    val redmineIssue = new Issue
    val time = getTime
    redmineIssue.setStartDate(time)
    val task = get().convertToGenericTask(redmineIssue)
    assertEquals(time, task.getValue(AllFields.startDate))
  }

  private def getTime = {
    val calendar = Calendar.getInstance
    calendar.set(2014, Calendar.APRIL, 23, 0, 0, 0)
    calendar.getTime
  }

  it("dueDateIsConverted") {
    val redmineIssue = new Issue
    val time = getTime
    redmineIssue.setDueDate(time)
    val task = get().convertToGenericTask(redmineIssue)
    assertEquals(time, task.getValue(AllFields.dueDate))
  }

  it("createdOnIsConverted") {
    val redmineIssue = new Issue
    val time = getTime
    redmineIssue.setCreatedOn(time)
    val task = get().convertToGenericTask(redmineIssue)
    assertEquals(time, task.getValue(AllFields.createdOn))
  }

  it("updatedOnIsConverted") {
    val redmineIssue = new Issue
    val time = getTime
    redmineIssue.setUpdatedOn(time)
    val task = get().convertToGenericTask(redmineIssue)
    assertEquals(time, task.getValue(AllFields.updatedOn))
  }

  it("priorityIsAssignedDefaultValueIfNotSet") {
    val redmineIssue = new Issue
    val task = get().convertToGenericTask(redmineIssue)
    assertEquals(Priorities.DEFAULT_PRIORITY_VALUE, task.getValue(AllFields.priority))
  }

  it("priorityIsConvertedIfSet") {
    val redmineIssue = new Issue
    redmineIssue.setPriorityText("High")
    val task = get().convertToGenericTask(redmineIssue)
    assertEquals(700, task.getValue(AllFields.priority))
  }

  it("priorityIsAssignedDefaultValueIfUnknownValueSet") {
    val redmineIssue = new Issue
    redmineIssue.setPriorityText("some unknown text")
    val task = get().convertToGenericTask(redmineIssue)
    assertEquals(Priorities.DEFAULT_PRIORITY_VALUE, task.getValue(AllFields.priority))
  }

  it("relations are converted") {
    val redmineIssue = IssueFactory.create(10)
    val relation = IssueRelationFactory.create
    relation.setType(IssueRelation.TYPE.precedes.toString)
    relation.setIssueId(10)
    relation.setIssueToId(20)
    redmineIssue.addRelations(Collections.singletonList(relation))
    val task = get().convertToGenericTask(redmineIssue)
    assertEquals(1, task.getRelations.size)
    val gRelation = task.getRelations.get(0)

    gRelation.getTaskId.getId shouldBe 10
    gRelation.getRelatedTaskId.getId shouldBe 20
    gRelation.getType shouldBe GRelationType.precedes
  }

}
