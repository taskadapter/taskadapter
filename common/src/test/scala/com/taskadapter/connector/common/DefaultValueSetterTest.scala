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

  it("field is safely skipped if target field is not defined") {
    val rows = List(new FieldRow(Some(Description), None, "default"))
    val newTask = DefaultValueSetter.adapt(rows,
      new GTask().setValue(Description, ""))
    newTask.getValue(Description) shouldBe null
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

  it("sets default values with proper types") {
    checkField(AssigneeLoginName, "login", "login")
    checkField(AssigneeFullName, "name", "name")
    checkField(Priority, "1", 1)
    checkDate(ClosedOn, "2018 05 04")
    checkField(Components, "c1 c2", Seq("c1", "c2"))
    checkField(Components, "c1", Seq("c1"))
    checkDate(CustomDate("date1"), "2018 05 04")
    checkField(CustomFloat("float"), "1.2", 1.2f)
    checkField(CustomSeqString("elements"), "a b", Seq("a", "b"))
    checkField(CustomString("custom1"), "text", "text")
    checkDate(CreatedOn, "2018 05 04")
    checkField(Description, "text", "text")
    checkDate(DueDate, "2018 05 04")
    checkField(DueDate, "", null)
    checkField(DoneRatio, "33", 33)
    checkField(EstimatedTime, "10.5", 10.5f)
    checkField(SpentTime, "3.3", 3.3f)
    checkField(Id, "5", 5)
    checkField(Key, "TEST-1", "TEST-1")
    checkDate(StartDate, "2018 05 04")
    checkField(Summary, "text", "text")
    checkField(TargetVersion, "1.0", "1.0")
    checkField(TaskStatus, "new", "new")
    checkField(TaskType, "feature", "feature")
    checkField(ReporterLoginName, "login", "login")
    checkField(ReporterFullName, "name", "name")
    checkDate(UpdatedOn, "2018 05 04")
  }

  private def checkDate(field: Field[_], str: String): Unit = {
    checkField(field, str, DateTypeTag.DATE_PARSER.parse(str))
  }

  private def checkField(field: Field[_], defaultString: String, expectedValue: Any): Unit = {
    val rows = Seq(FieldRow(field.asInstanceOf[Field[Any]], field.asInstanceOf[Field[Any]], defaultString))
    val newTask = DefaultValueSetter.adapt(rows, new GTask)
    newTask.getValue(field) shouldBe expectedValue
  }

}


