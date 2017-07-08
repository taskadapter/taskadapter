package com.taskadapter.connector.common

import com.taskadapter.connector.FieldRow
import com.taskadapter.model.GTask
import org.fest.assertions.Assertions.assertThat
import org.junit.runner.RunWith
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

import scala.collection.JavaConverters._
import scala.collection.mutable._

@RunWith(classOf[JUnitRunner])
class DefaultValueSetterTest extends FunSpec with ScalaFutures with Matchers {
  it("task is deep cloned") {
    val originalTask = new GTask
    originalTask.setValue("description", "original description")

    val rows = List(
      FieldRow(true, "summary", "summary", ""),
      FieldRow(true, "description", "description", "")
    )

    val newTask = DefaultValueSetter.adapt(rows.asJava, originalTask)
    originalTask.setValue("description", "new description")
    assertThat(newTask.getValue("description")).isEqualTo("original description")

  }

  it("default value is set if field is empty") {
    val rows = List(
      FieldRow(true, "summary", "summary", ""),
      FieldRow(true, "description", "description", "default description")
    )
    val originalTask = new GTask
    originalTask.setValue("description", "")

    val newTask = DefaultValueSetter.adapt(rows.asJava, originalTask)
    newTask.getValue("description") shouldBe "default description"
  }

  it("existing value is preserved when field has it") {
    val rows = List(
      FieldRow(true, "summary", "summary", ""),
      FieldRow(true, "description", "description", "default description")
    )
    val originalTask = new GTask
    originalTask.setValue("description", "something")
    val newTask = DefaultValueSetter.adapt(rows.asJava, originalTask)
    newTask.getValue("description") shouldBe "something"
  }

  // without this creating subtasks won't work, at least in JIRA
  it("parent key is preserved") {
    val rows = List(
      FieldRow(true, "summary", "summary", ""),
    )
    val task = new GTask
    task.setParentKey("parent1")
    val newTask = DefaultValueSetter.adapt(rows.asJava, task)
    newTask.getParentKey shouldBe "parent1"
  }
}


