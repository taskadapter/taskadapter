package com.taskadapter.connector.basecamp.classic

import com.taskadapter.connector.basecamp.classic.transport.{BaseCommunicator, ObjectAPIFactory}
import org.scalatest.FunSpec

// TODO identical to Basecamp 2 code
trait BasecampClassicTestProject extends FunSpec {
  private val factory = new ObjectAPIFactory(new BaseCommunicator)
  val config = TestBasecampConfig.config
  val setup = TestBasecampConfig.setup

  def withTempProject(testCode: String => Any) {
    val someId = System.currentTimeMillis()
    val todoList = BasecampUtils.createTodoList(factory, config, setup, "list " + someId, "")
    config.setTodoKey(todoList.getKey())
    try {
      testCode(todoList.getKey())
    }
    finally {
      BasecampUtils.deleteTodoList(factory, config, setup)
    }
  }

}
