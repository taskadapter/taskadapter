package com.taskadapter.connector.common

import java.util.Date

import com.taskadapter.connector.definition.TaskId
import com.taskadapter.connector.{Field, FieldRow}
import com.taskadapter.model.GTask
import org.fest.assertions.Assertions.assertThat
import org.junit.runner.RunWith
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

import scala.collection.JavaConverters._

@RunWith(classOf[JUnitRunner])
class DefaultValueSetterTest extends FunSpec with ScalaFutures with Matchers {
  it("task is deep cloned") {
    val originalTask = new GTask
    originalTask.setValue("description", "original description")

    val rows = List(
      FieldRow(Field("summary"), Field("summary"), ""),
      FieldRow(Field("description"), Field("description"), "")
    )

    val newTask = DefaultValueSetter.adapt(rows.asJava, originalTask)
    originalTask.setValue("description", "new description")
    assertThat(newTask.getValue("description")).isEqualTo("original description")

  }

  it("default value is set if field is empty") {
    val rows = List(
      FieldRow(Field("summary"), Field("summary"), ""),
      FieldRow(Field("description"), Field("description"), "default description")
    )
    val originalTask = new GTask
    originalTask.setValue("description", "")

    val newTask = DefaultValueSetter.adapt(rows.asJava, originalTask)
    newTask.getValue("description") shouldBe "default description"
  }

  it("existing value is preserved when field has it") {
    val rows = List(
      FieldRow(Field("summary"), Field("summary"), ""),
      FieldRow(Field("description"), Field("description"), "default description")
    )
    val originalTask = new GTask
    originalTask.setValue("description", "something")
    val newTask = DefaultValueSetter.adapt(rows.asJava, originalTask)
    newTask.getValue("description") shouldBe "something"
  }

  // without this creating subtasks won't work, at least in JIRA
  it("parent key is preserved") {
    val rows = List(
      FieldRow(Field("summary"), Field("summary"), ""),
    )
    val task = new GTask
    task.setParentIdentity(TaskId(1, "parent1"))
    val newTask = DefaultValueSetter.adapt(rows.asJava, task)
    newTask.getParentIdentity shouldBe "parent1"
  }

  it("Date field type is adapted") {
    val rows = List(
      FieldRow(Field.date("Due date"), Field.date("Due date"), ""),
    )
    val task = new GTask
    val date = new Date
    task.setValue("Due date", date)
    val newTask = DefaultValueSetter.adapt(rows.asJava, task)
    newTask.getValue("Due date") shouldBe date
  }
}


