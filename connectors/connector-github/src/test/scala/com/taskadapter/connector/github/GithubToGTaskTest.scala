package com.taskadapter.connector.github

import com.taskadapter.connector.testlib.TestDataLoader
import com.taskadapter.model.Summary
import org.eclipse.egit.github.core.Issue
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class GithubToGTaskTest extends FunSpec with Matchers {
  it("converts a basic task") {
    val issue = TestDataLoader.load("issue.json", classOf[Issue]).asInstanceOf[Issue]
    val task = GithubToGTask.toGtask(issue)
    assertEquals("task 1", task.getValue(Summary))
  }
}