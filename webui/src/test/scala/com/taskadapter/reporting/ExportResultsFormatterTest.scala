package com.taskadapter.reporting

import com.taskadapter.connector.definition.TaskId
import com.taskadapter.web.uiapi.DecodedTaskError
import org.junit.runner.RunWith
import org.scalatest.{FunSpec, Matchers}
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ExportResultsFormatterTest extends FunSpec with Matchers {

  val id1 = TaskId(1, "key1")
  val id2 = TaskId(2, "key2")
  val id3 = TaskId(3, "key3")

  val item1 = new DecodedTaskError(id1, "error", "exception")
  val item1SameError = new DecodedTaskError(id2, "error", "exception")
  val item3 = new DecodedTaskError(id3, "error", "another exception")

  it("replaces duplicate exceptions") {
    ExportResultsFormatter.formatTaskErrors(Seq(item1, item1SameError)) shouldBe
      orig(id1) + line + same(id2)
  }

  it("replaces two duplicate exceptions") {
    ExportResultsFormatter.formatTaskErrors(Seq(
      new DecodedTaskError(id1, "error", "exception"),
      new DecodedTaskError(id2, "error", "exception"),
      new DecodedTaskError(id3, "error", "exception"))) shouldBe
      orig(id1) + line + same(id2) + line + same(id3)
  }

  it("leaves single element as is") {
    ExportResultsFormatter.formatTaskErrors(Seq(item1)) shouldBe orig(id1)
  }

  it("leaves different element in place") {
    ExportResultsFormatter.formatTaskErrors(Seq(item1, item3)) shouldBe
      orig(id1) + line + other(id3)
  }

  it("replaces duplicate and leaves different one in place") {
    ExportResultsFormatter.formatTaskErrors(Seq(item1, item1SameError, item3)) shouldBe
      orig(id1) + line + same(id2) + line + other(id3)
  }
  it("empty list gives empty string") {
    ExportResultsFormatter.formatTaskErrors(Seq()) shouldBe ""
  }

  def orig(id: TaskId): String = {
    s"$id - error - exception"
  }

  def same(id: TaskId): String = {
    s"$id - error - same as previous"
  }

  def other(id: TaskId): String = {
    s"$id - error - another exception"
  }

  private def line = System.lineSeparator()
}
