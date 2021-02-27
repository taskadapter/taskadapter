package com.taskadapter.connector.basecamp.classic

import java.util

import com.taskadapter.connector.basecamp.classic.transport.ObjectAPIFactory
import com.taskadapter.connector.common.TaskSavingUtils
import com.taskadapter.connector.definition._
import com.taskadapter.connector.definition.exceptions.ConnectorException
import com.taskadapter.connector.{FieldRow, NewConnector}
import com.taskadapter.core.PreviouslyCreatedTasksResolver
import com.taskadapter.model.{GTask, GUser}

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

object BasecampClassicConnector {
  /**
    * Keep it the same to enable backward compatibility for previously created config files.
    */
  var ID = "Basecamp Classic"
}

class BasecampClassicConnector(config: BasecampClassicConfig, setup: WebConnectorSetup, factory: ObjectAPIFactory) extends NewConnector {
  val api = factory.createObjectAPI( setup)

  def loadData(): util.List[GTask] = {
    BasecampConfigValidator.validateServerAuth(setup)
    BasecampConfigValidator.validateTodoList(config)
    val obj = api.getObject("todo_lists/" + config.getTodoKey + ".xml")
    val ilist = XmlUtils.getOptElt(obj, "todo-items")
    if (ilist == null) return new util.ArrayList[GTask]

    val todosobject = XmlUtils.getDirectAncestors(ilist, "todo-item")
    val res = new util.ArrayList[GTask](todosobject.size)
    for (ee <- todosobject) {
      res.add(BasecampClassicToGTask.parseTask(ee))
    }
    res
  }

  override def loadTaskByKey(id: TaskId, rows: java.lang.Iterable[FieldRow[_]]): GTask = {
    BasecampConfigValidator.validateServerAuth(setup)
    val obj = api.getObject("todo_items/" + id.getKey + ".xml")
    BasecampClassicToGTask.parseTask(obj)
  }

  @throws[ConnectorException]
  override def saveData(previouslyCreatedTasks: PreviouslyCreatedTasksResolver, tasks: util.List[GTask],
               monitor: ProgressMonitor,
               fieldRows: java.lang.Iterable[FieldRow[_]]): SaveResult = try {
    BasecampConfigValidator.validateServerAuth(setup)
    BasecampConfigValidator.validateTodoList(config)
    val users = loadUsers()
    val converter = new GTaskToBasecampClassic(users)
    val saver = new BasecampClassicSaver(api, config)
    val resultBuilder = TaskSavingUtils.saveTasks(previouslyCreatedTasks, tasks, converter, saver, monitor, fieldRows,
      setup.getHost)

    resultBuilder.getResult
  }

  @throws[ConnectorException]
  private def loadUsers(): Seq[GUser] = {
    if (config.isLookupUsersByName) {
      val arr = XmlUtils.getDirectAncestors(api.getObject("people.xml"), "person")
      arr.asScala.map(BasecampUtils.parseUser)
    } else {
      Seq()
    }
  }
}