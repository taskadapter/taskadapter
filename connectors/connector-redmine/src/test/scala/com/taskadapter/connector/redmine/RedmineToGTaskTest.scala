package com.taskadapter.connector.redmine

import java.util.{Calendar, Collections}

import com.taskadapter.connector.Priorities
import com.taskadapter.connector.definition.TaskId
import com.taskadapter.model._
import com.taskadapter.redmineapi.bean._
import org.junit.Assert.{assertEquals, assertNull}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSpec, Matchers}

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
    assertEquals("text 1", task.getValue(Summary))
  }

  it("descriptionIsConverted") {
    val redmineIssue = new Issue
    redmineIssue.setDescription("description 1")
    val task = get().convertToGenericTask(redmineIssue)
    assertEquals("description 1", task.getValue(Description))
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
    task.getParentIdentity shouldBe TaskId(123, "123")
  }

  it("parentIdIsIgnoredIfNotSet") {
    val redmineIssue = new Issue
    val task = get().convertToGenericTask(redmineIssue)
    assertNull(task.getParentIdentity)
  }

  it("assigneeIsIgnoredIfNotSet") {
    val redmineIssue = new Issue
    val task = get().convertToGenericTask(redmineIssue)
    assertNull(task.getValue(AssigneeFullName))
    assertNull(task.getValue(AssigneeLoginName))
  }

  it("trackerTypeIsConvertedIfSet") {
    val redmineIssue = new Issue
    val tracker = TrackerFactory.create(123, "something")
    redmineIssue.setTracker(tracker)
    val task = get().convertToGenericTask(redmineIssue)
    assertEquals("something", task.getValue(TaskType))
  }

  it("trackerTypeIsIgnoredIfNotSet") {
    val redmineIssue = new Issue
    val task = get().convertToGenericTask(redmineIssue)
    assertNull(task.getValue(TaskType))
  }

  it("statusIsConverted") {
    val redmineIssue = new Issue
    redmineIssue.setStatusName("some status")
    val task = get().convertToGenericTask(redmineIssue)
    assertEquals("some status", task.getValue(TaskStatus))
  }

  it("estimatedHoursAreConverted") {
    val redmineIssue = new Issue
    redmineIssue.setEstimatedHours(55f)
    val task = get().convertToGenericTask(redmineIssue)
    task.getValue(EstimatedTime) shouldBe 55f
  }

  it("doneRatioIsConverted") {
    val redmineIssue = new Issue
    redmineIssue.setDoneRatio(75)
    val task = get().convertToGenericTask(redmineIssue)
    task.getValue(DoneRatio) shouldBe 75f
  }

  it("startDateIsConverted") {
    val redmineIssue = new Issue
    val time = getTime
    redmineIssue.setStartDate(time)
    val task = get().convertToGenericTask(redmineIssue)
    assertEquals(time, task.getValue(StartDate))
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
    assertEquals(time, task.getValue(DueDate))
  }

  it("createdOnIsConverted") {
    val redmineIssue = new Issue
    val time = getTime
    redmineIssue.setCreatedOn(time)
    val task = get().convertToGenericTask(redmineIssue)
    assertEquals(time, task.getValue(CreatedOn))
  }

  it("updatedOnIsConverted") {
    val redmineIssue = new Issue
    val time = getTime
    redmineIssue.setUpdatedOn(time)
    val task = get().convertToGenericTask(redmineIssue)
    assertEquals(time, task.getValue(UpdatedOn))
  }

  it("priorityIsAssignedDefaultValueIfNotSet") {
    val redmineIssue = new Issue
    val task = get().convertToGenericTask(redmineIssue)
    assertEquals(Priorities.DEFAULT_PRIORITY_VALUE, task.getValue(Priority))
  }

  it("priorityIsConvertedIfSet") {
    val redmineIssue = new Issue
    redmineIssue.setPriorityText("High")
    val task = get().convertToGenericTask(redmineIssue)
    assertEquals(700, task.getValue(Priority))
  }

  it("priorityIsAssignedDefaultValueIfUnknownValueSet") {
    val redmineIssue = new Issue
    redmineIssue.setPriorityText("some unknown text")
    val task = get().convertToGenericTask(redmineIssue)
    assertEquals(Priorities.DEFAULT_PRIORITY_VALUE, task.getValue(Priority))
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

    gRelation.taskId.id shouldBe 10
    gRelation.relatedTaskId.id shouldBe 20
    gRelation.`type` shouldBe Precedes
  }

}
