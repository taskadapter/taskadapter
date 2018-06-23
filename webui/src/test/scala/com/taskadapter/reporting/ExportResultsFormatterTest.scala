package com.taskadapter.reporting

import com.taskadapter.connector.definition.TaskId
import org.junit.runner.RunWith
import org.scalatest.{FunSpec, Matchers}
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ExportResultsFormatterTest extends FunSpec with Matchers {

  val item1 = (TaskId(1, "key1"), "error", "exception")
  val item1Text = "TaskId(1,key1) - error - exception"

  val item1SameError = (TaskId(2, "key2"), "error", "exception")
  val item1SameErrorText = "TaskId(2,key2) - error - same as previous"

  val item2 = (TaskId(3, "key3"), "error", "another exception")
  val item2Text = "TaskId(3,key3) - error - another exception"

  it("replaces duplicate exceptions") {
    ExportResultsFormatter.formatTaskErrors(Seq(item1, item1SameError)) shouldBe
      item1Text + System.lineSeparator() + item1SameErrorText
  }
  it("leaves single element as is") {
    ExportResultsFormatter.formatTaskErrors(Seq(item1)) shouldBe item1Text
  }

  it("leaves different element in place") {
    ExportResultsFormatter.formatTaskErrors(Seq(item1, item2)) shouldBe
      item1Text + System.lineSeparator() + item2Text
  }

  it("replaces duplicate and leaves different one in place") {
    ExportResultsFormatter.formatTaskErrors(Seq(item1, item1SameError, item2)) shouldBe
      item1Text + System.lineSeparator() + item1SameErrorText + System.lineSeparator() + item2Text
  }
  it("empty list gives empty string") {
    ExportResultsFormatter.formatTaskErrors(Seq()) shouldBe ""
  }
}
