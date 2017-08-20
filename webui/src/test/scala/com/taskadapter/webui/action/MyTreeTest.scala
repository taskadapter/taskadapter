package com.taskadapter.webui.action

import com.taskadapter.connector.definition.TaskId
import com.taskadapter.core.PreviouslyCreatedTasksResolver
import com.taskadapter.model.GTask
import com.vaadin.ui.CheckBox
import org.junit.Assert
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

import scala.collection.JavaConverters._

@RunWith(classOf[JUnitRunner])
class MyTreeTest extends FunSpec with Matchers {

  val task0 = createTestTask(0l,
    createTestTask(1l), createTestTask(2l),
    createTestTask(3l,
      createTestTask(4l),
      createTestTask(5l,
        createTestTask(6l))
    ),
    createTestTask(7l),
    createTestTask(8l,
      createTestTask(9l)
    )
  )
  var rootLevelTasks = Seq(task0)

  val resolver = PreviouslyCreatedTasksResolver.empty

  private def createTestTask(id: Long, children: GTask*) = {
    val task = new GTask
    task.setId(id)
    task.setKey(id + "")
    task.setChildren(children.asJava)
    task
  }

  it("all tasks are selected by default") {
    val myTree = new MyTree(resolver, rootLevelTasks.asJava, "some location")
    val selectedGTaskList = myTree.getSelectedRootLevelTasks
    Assert.assertNotNull(selectedGTaskList)
    selectedGTaskList.asScala shouldBe rootLevelTasks
  }

  val expectedRootLevelTasks1 = Seq(createTestTask(0l, createTestTask(1l), createTestTask(2l),
    createTestTask(7l), createTestTask(8l, createTestTask(9l)))
  )

  it("all children are unselected when parent is unselected") {
    val myTree = new MyTree(resolver, rootLevelTasks.asJava, "some location")
    // deselect parent #3
    myTree.tree.getContainerProperty(TaskId(3l, "3"), MyTree.ACTION_PROPERTY).getValue.asInstanceOf[CheckBox].setValue(false)
    val selectedGTaskList = myTree.getSelectedRootLevelTasks
    Assert.assertNotNull(selectedGTaskList)
    selectedGTaskList.asScala shouldBe expectedRootLevelTasks1
  }

  it("all parents up the hierarchy must be selected if at least one child is selected") {
    val myTree = new MyTree(resolver, rootLevelTasks.asJava, "some location")
    // deselect parent #0
    myTree.tree.getContainerProperty(TaskId(0l, "0"), MyTree.ACTION_PROPERTY).getValue.asInstanceOf[CheckBox].setValue(false)
    // select child #6
    myTree.tree.getContainerProperty(TaskId(6l, "6"), MyTree.ACTION_PROPERTY).getValue.asInstanceOf[CheckBox].setValue(true)
    val selectedGTaskList = myTree.getSelectedRootLevelTasks
    Assert.assertNotNull(selectedGTaskList)
    selectedGTaskList.asScala.toList shouldBe expectedRootLevelTasks2.toList
  }

  val expectedRootLevelTasks2 = Seq(
    createTestTask(0l, createTestTask(3l, createTestTask(5l, createTestTask(6l))))
  )
}
