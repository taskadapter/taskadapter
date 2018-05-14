package com.taskadapter.connector.common

import java.util

import com.taskadapter.model.{GTask, Summary}
import org.junit.{Assert, Test}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}
import scala.collection.JavaConverters._

@RunWith(classOf[JUnitRunner])
class TreeUtilsTest extends FunSpec with Matchers {
  it("testCloneTree") {
    val tree = new util.ArrayList[GTask]
    val genericTask = new GTask
    genericTask.setValue(Summary, "genericTask")
    tree.add(genericTask)
    val sub1 = new GTask
    sub1.setValue(Summary, "sub1")
    val sub2 = new GTask
    sub2.setValue(Summary, "sub2")
    genericTask.getChildren.add(sub1)
    genericTask.getChildren.add(sub2)
    val cloned = TreeUtils.cloneTree(tree)
    val NEW_TEXT = "newtext"
    sub1.setValue(Summary, NEW_TEXT)
    val clonedGenericTask = cloned.get(0)
    val clonedSub1 = clonedGenericTask.getChildren.get(0)
    Assert.assertEquals(NEW_TEXT, sub1.getValue(Summary))
    Assert.assertEquals("sub1", clonedSub1.getValue(Summary))
  }

  it("shallowCloneSkipsChildren") {
    val task = new GTask
    val id = 101l
    val summary = "some summary here"
    task.setId(id)
    task.setValue(Summary, summary)
    val child1 = new GTask
    child1.setId(1011l)
    child1.setValue(Summary, "child summary")
    task.getChildren.add(child1)
    val clonedTask = TreeUtils.createShallowCopyWithoutChildren(task)
    // TODO add more fields to check here
    Assert.assertTrue(clonedTask.getId == id)
    Assert.assertTrue(clonedTask.getValue(Summary) == summary)
    // the copy constructor does NOT copy children -
    // this fact is used in some places in the code, so need to check in the test
    Assert.assertTrue(clonedTask.getChildren.isEmpty)
  }

  it("converts flat list to parent with children") {
    val task = new GTask
    task.setValue(Summary, "genericTask")
    val sub1 = new GTask
    sub1.setValue(Summary, "sub1")
    val sub2 = new GTask
    sub2.setValue(Summary, "sub2")
    task.addChildTask(sub1)
    task.addChildTask(sub2)
    val tree = TreeUtils.buildTreeFromFlatList(List(task, sub1, sub2).asJava)

    tree.asScala.head.getChildren.size() shouldBe 2
  }
}


