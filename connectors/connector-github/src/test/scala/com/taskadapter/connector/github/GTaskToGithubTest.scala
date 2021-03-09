package com.taskadapter.connector.github

import com.taskadapter.model.{AllFields, GTask, GTaskBuilder}
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class GTaskToGithubTest extends FunSpec with Matchers {
  describe("assignee") {
    it("empty assignee is ignored") {
      val task = new GTask().setValue(AllFields.summary, "my").setValue(AllFields.assigneeLoginName, null)
      val issue = getConverter.toIssue(task)
      assertEquals("my", issue.getTitle)
    }

    it("assignee with null Login Name is ignored") {
      val task = new GTask
      task.setValue(AllFields.summary, "my")
      task.setValue(AllFields.assigneeLoginName, null)
      val issue = getConverter.toIssue(task)
      assertEquals("my", issue.getTitle)
    }

    it("assignee is set") {
      val task = new GTaskBuilder().withAssigneeLogin("login").build()
      val issue = getConverter.toIssue(task)
      issue.getAssignee.getLogin shouldBe "login"
    }
  }

  it("summaryIsConvertedByDefault") {
    val issue = getConverter.toIssue(createTask("summary1"))
    assertEquals("summary1", issue.getTitle)
  }

  it("descriptionIsConvertedByDefault") {
    val issue = getConverter.toIssue(createTask("summary1", "descr1"))
    assertEquals("descr1", issue.getBody)
  }

  private def getConverter = {
    new GTaskToGithub(new MockUserService())
  }

  private def createTask(summary: String): GTask = createTask(summary, null)

  private def createTask(summary: String, description: String): GTask = {
    new GTask().setValue(AllFields.summary, summary).setValue(AllFields.description, description)
  }
}