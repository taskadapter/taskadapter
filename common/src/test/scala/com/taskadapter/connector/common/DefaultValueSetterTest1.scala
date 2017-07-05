package com.taskadapter.connector.common

import com.taskadapter.model.{GTask, GTaskDescriptor}
import org.fest.assertions.Assertions.assertThat
import org.junit.runner.RunWith
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

import scala.collection.JavaConverters._
import scala.collection.mutable._

@RunWith(classOf[JUnitRunner])
class DefaultValueSetterTest1 extends FunSpec with ScalaFutures with Matchers {
  describe("DefaultValueSetter") {
    it("task is deep cloned") {
      val setter = new DefaultValueSetter(Map.empty[String, String].asJava)
      val originalTask: GTask = new GTask
      originalTask.setType("original type")
      originalTask.setDescription("original description")
      // TODO REVIEW Does this method perform a shallow copy or a deep copy?
      val newTask: GTask = setter.cloneAndReplaceEmptySelectedFieldsWithDefaultValues(originalTask)
      originalTask.setType("new type")
      originalTask.setDescription("new description")
      assertThat(newTask.getType).isEqualTo("original type")
      assertThat(newTask.getDescription).isEqualTo("original description")
    }
  }
  it("default value is set if field is empty") {
    val setter = new DefaultValueSetter(Map(GTaskDescriptor.FIELD.DESCRIPTION.name -> "default description").asJava)
    val originalTask = new GTask
    originalTask.setDescription("")
    val newTask = setter.cloneAndReplaceEmptySelectedFieldsWithDefaultValues(originalTask)
    newTask.getDescription shouldBe "default description"
  }

  it("existing value is preserved when field has it") {
    val setter = new DefaultValueSetter(Map(GTaskDescriptor.FIELD.DESCRIPTION.name -> "default description").asJava)
    val originalTask = new GTask
    originalTask.setDescription("something")
    val newTask = setter.cloneAndReplaceEmptySelectedFieldsWithDefaultValues(originalTask)
    newTask.getDescription shouldBe "something"
  }

}


