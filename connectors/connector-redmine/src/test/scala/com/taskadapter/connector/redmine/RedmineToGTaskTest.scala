package com.taskadapter.connector.redmine

import java.util.{Calendar, Collections}

import com.taskadapter.connector.Priorities
import com.taskadapter.model.{GRelation, GUser}
import com.taskadapter.redmineapi.bean._
import org.junit.Assert.{assertEquals, assertNull}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class RedmineToGTaskTest extends FunSpec with Matchers with BeforeAndAfter with BeforeAndAfterAll {

  private def get(): RedmineToGTask = {
    val config = new RedmineConfig
    new RedmineToGTask(config)
  }

  it("summaryIsConverted") {
    val redmineIssue = new Issue
    redmineIssue.setSubject("text 1")
    val task = get().convertToGenericTask(redmineIssue)
    assertEquals("text 1", task.getValue(RedmineField.summary))
  }

  it("descriptionIsConverted") {
    val redmineIssue = new Issue
    redmineIssue.setDescription("description 1")
    val task = get().convertToGenericTask(redmineIssue)
    assertEquals("description 1", task.getValue(RedmineField.description))
  }

  it("idIsConvertedIfSet") {
    val redmineIssue = IssueFactory.create(123)
    val task = get().convertToGenericTask(redmineIssue)
    assertEquals(123.asInstanceOf[Integer], task.getId)
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
    assertEquals("123", task.getParentKey)
  }

  it("parentIdIsIgnoredIfNotSet") {
    val redmineIssue = new Issue
    val task = get().convertToGenericTask(redmineIssue)
    assertNull(task.getParentKey)
  }

  it("assigneeIsIgnoredIfNotSet") {
    val redmineIssue = new Issue
    val task = get().convertToGenericTask(redmineIssue)
    assertNull(task.getValue(RedmineField.assignee))
  }

  it("assigneeIsConvertedIfSet") {
    val redmineIssue = new Issue
    val assignee = UserFactory.create
    assignee.setLogin("mylogin")
    redmineIssue.setAssignee(assignee)
    val task = get().convertToGenericTask(redmineIssue)
    val loadedAssignee = task.getValue(RedmineField.assignee).asInstanceOf[GUser]
    assertEquals("mylogin", loadedAssignee.getLoginName)
  }

  it("trackerTypeIsConvertedIfSet") {
    val redmineIssue = new Issue
    val tracker = TrackerFactory.create(123, "something")
    redmineIssue.setTracker(tracker)
    val task = get().convertToGenericTask(redmineIssue)
    assertEquals("something", task.getValue(RedmineField.taskType))
  }

  it("trackerTypeIsIgnoredIfNotSet") {
    val redmineIssue = new Issue
    val task = get().convertToGenericTask(redmineIssue)
    assertNull(task.getValue(RedmineField.taskType))
  }

  it("statusIsConverted") {
    val redmineIssue = new Issue
    redmineIssue.setStatusName("some status")
    val task = get().convertToGenericTask(redmineIssue)
    assertEquals("some status", task.getValue(RedmineField.taskStatus))
  }

  it("estimatedHoursAreConverted") {
    val redmineIssue = new Issue
    redmineIssue.setEstimatedHours(55f)
    val task = get().convertToGenericTask(redmineIssue)
    assertEquals(55f.asInstanceOf[Float], task.getValue(RedmineField.estimatedTime))
  }

  it("doneRatioIsConverted") {
    val redmineIssue = new Issue
    redmineIssue.setDoneRatio(75)
    val task = get().convertToGenericTask(redmineIssue)
    assertEquals(75, task.getValue(RedmineField.doneRatio))
  }

  it("startDateIsConverted") {
    val redmineIssue = new Issue
    val time = getTime
    redmineIssue.setStartDate(time)
    val task = get().convertToGenericTask(redmineIssue)
    assertEquals(time, task.getValue(RedmineField.startDate))
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
    assertEquals(time, task.getValue(RedmineField.dueDate))
  }

  it("createdOnIsConverted") {
    val redmineIssue = new Issue
    val time = getTime
    redmineIssue.setCreatedOn(time)
    val task = get().convertToGenericTask(redmineIssue)
    assertEquals(time, task.getValue(RedmineField.createdOn))
  }

  it("updatedOnIsConverted") {
    val redmineIssue = new Issue
    val time = getTime
    redmineIssue.setUpdatedOn(time)
    val task = get().convertToGenericTask(redmineIssue)
    assertEquals(time, task.getValue(RedmineField.updatedOn))
  }

  it("priorityIsAssignedDefaultValueIfNotSet") {
    val redmineIssue = new Issue
    val task = get().convertToGenericTask(redmineIssue)
    assertEquals(Priorities.DEFAULT_PRIORITY_VALUE, task.getValue(RedmineField.priority))
  }

  it("priorityIsConvertedIfSet") {
    val redmineIssue = new Issue
    redmineIssue.setPriorityText("High")
    val task = get().convertToGenericTask(redmineIssue)
    assertEquals(700, task.getValue(RedmineField.priority))
  }

  it("priorityIsAssignedDefaultValueIfUnknownValueSet") {
    val redmineIssue = new Issue
    redmineIssue.setPriorityText("some unknown text")
    val task = get().convertToGenericTask(redmineIssue)
    assertEquals(Priorities.DEFAULT_PRIORITY_VALUE, task.getValue(RedmineField.priority))
  }

  it("relationsAreConverted") {
    val redmineIssue = IssueFactory.create(10)
    //        Issue blockedIssue = IssueFactory.create(20);
    val relation = IssueRelationFactory.create
    relation.setType(IssueRelation.TYPE.precedes.toString)
    relation.setIssueId(10)
    relation.setIssueToId(20)
    redmineIssue.addRelations(Collections.singletonList(relation))
    val task = get().convertToGenericTask(redmineIssue)
    assertEquals(1, task.getRelations.size)
    val gRelation = task.getRelations.get(0)
    assertEquals("10", gRelation.getTaskKey)
    assertEquals("20", gRelation.getRelatedTaskKey)
    assertEquals(GRelation.TYPE.precedes, gRelation.getType)
    assertNull(gRelation.getDelay)
  }

}
