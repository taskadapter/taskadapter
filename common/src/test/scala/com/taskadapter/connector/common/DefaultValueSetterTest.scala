package com.taskadapter.connector.common

import java.util.Date

import com.taskadapter.connector.FieldRow
import com.taskadapter.connector.definition.TaskId
import com.taskadapter.model._
import org.fest.assertions.Assertions.assertThat
import org.junit.runner.RunWith
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

import scala.collection.JavaConverters._

@RunWith(classOf[JUnitRunner])
class DefaultValueSetterTest extends FunSpec with ScalaFutures with Matchers {
  val defaultRows = List(
    FieldRow(Summary, Summary, ""),
    FieldRow(Description, Description, "")
  )

  it("task is deep cloned") {
    val originalTask = new GTask
    originalTask.setValue(Description, "original description")

    val newTask = DefaultValueSetter.adapt(defaultRows, originalTask)
    originalTask.setValue(Description, "new description")
    assertThat(newTask.getValue(Description)).isEqualTo("original description")

  }

  it("default value is set if source field value is empty") {
    val rows = List(
      FieldRow(Summary, Summary, ""),
      FieldRow(Description, Description, "default description")
    )
    val originalTask = new GTask
    originalTask.setValue(Description, "")

    val newTask = DefaultValueSetter.adapt(rows, originalTask)
    newTask.getValue(Description) shouldBe "default description"
  }

  // TA supports one-sided mappings where a default value is provided to the target Field and there is
  // no corresponding Field in the source connector
  it("default value is set if source field is not defined but default value exists") {
    val rows = List(
      new FieldRow(None, Some(Description), "default description")
    )
    val originalTask = new GTask
    originalTask.setValue(Description, "")

    val newTask = DefaultValueSetter.adapt(rows, originalTask)
    newTask.getValue(Description) shouldBe "default description"
  }

  it("existing value is preserved when field has it") {
    val rows = List(
      FieldRow(Summary, Summary, ""),
      FieldRow(Description, Description, "default description")
    )
    val originalTask = new GTask
    originalTask.setValue(Description, "something")
    val newTask = DefaultValueSetter.adapt(rows, originalTask)
    newTask.getValue(Description) shouldBe "something"
  }

  // without this creating subtasks won't work, at least in JIRA
  it("parent key is preserved") {
    val task = new GTask
    val identity = TaskId(1, "parent1")
    task.setParentIdentity(identity)
    val newTask = DefaultValueSetter.adapt(defaultRows, task)
    newTask.getParentIdentity shouldBe identity
  }

  // regression test for https://bitbucket.org/taskadapter/taskadapter/issues/85/subtasks-are-not-saved
  it("children are preserved") {
    val parent = new GTask
    parent.setId(1l)
    val sub = new GTask
    sub.setId(100l)
    parent.addChildTask(sub)

    val adapted = DefaultValueSetter.adapt(defaultRows, parent)
    adapted.getChildren.size() shouldBe 1
  }

  it("Date field type is adapted") {
    val rows = List(
      FieldRow(DueDate, DueDate, null)
    )
    val task = new GTask
    val date = new Date
    task.setValue(DueDate, date)
    val newTask = DefaultValueSetter.adapt(rows, task)
    newTask.getValue(DueDate) shouldBe date
  }
}


