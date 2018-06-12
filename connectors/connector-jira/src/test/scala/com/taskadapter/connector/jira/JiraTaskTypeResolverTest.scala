package com.taskadapter.connector.jira

import com.taskadapter.connector.definition.TaskId
import com.taskadapter.model.{GTask, GTaskBuilder, TaskType}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class JiraTaskTypeResolverTest extends FunSpec with Matchers {
  val someId = TaskId(1, "KEY1")
  val topLevel = "default parent"
  val sub = "default sub"
  val converter = GTaskToJiraFactory.getConverter()

  it("resolves default task type") {
    verify(GTaskBuilder.withSummary(), topLevel)
  }

  it("resolves default sub task type") {
    verify(GTaskBuilder.withSummary().setParentIdentity(someId), sub)
  }

  it("keeps explicit type") {
    verify(new GTask().setValue(TaskType, "Task"), "Task")
  }

  it("keeps explicit type even if parent is set") {
    verify(new GTask().setValue(TaskType, "Task").setParentIdentity(someId), "Task")
  }

  def verify(task: GTask, expected: String): Unit = {
    JiraTaskTypeResolver.resolveIssueTypeNameForCreate(converter.convertToJiraIssue(task),
      topLevel, sub) shouldBe expected
  }
}
