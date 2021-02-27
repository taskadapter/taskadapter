package com.taskadapter.model

import java.util.Collections

import org.junit.Assert.{assertFalse, assertTrue}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class GTaskTest extends FunSpec with Matchers {

  val field = Field("str")

  it("newTaskHasNoChildren") {
    val task = new GTask
    assertTrue("a new task must have no children", task.getChildren.isEmpty)
    assertFalse("a new task must have no children", task.hasChildren)
  }

  it("nullChildrenReturnsFalse") {
    val task = new GTask
    task.setChildren(null)
    assertFalse(task.hasChildren)
  }

  it("emptyChildrenListReturnsFalse") {
    val task = new GTask
    task.setChildren(Collections.emptyList[GTask])
    assertFalse(task.hasChildren)
  }

  it("hasChildrenReturnsTrueWithChildren") {
    val task = new GTask
    val child1 = new GTask
    child1.setId(1011l)
    task.getChildren.add(child1)
    assertTrue(task.hasChildren)
  }

  describe("constructor") {
    it("fields are deep cloned") {
      val task = new GTask()
      task.setValue(field, "123")
      val cloned = new GTask(task)
      task.setValue(field, "updated")
      cloned.getValue(field) shouldBe "123"
    }

    it("sets empty collections") {
      val task = new GTask()
      task.getChildren shouldBe empty
      task.getRelations shouldBe empty
    }
  }

  describe("identity") {
    it("null Id converted to 0 in Identity") {
      new GTask().getIdentity.getId shouldBe 0
    }

    it("null Id stays null") {
      val task = new GTask()
      task.setId(null)
      task.getId shouldBe null
    }
  }
}