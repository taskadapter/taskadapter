package com.taskadapter.connector.basecamp

import java.util

import com.taskadapter.connector.basecamp.transport.ObjectAPIFactory
import com.taskadapter.connector.common.TaskSavingUtils
import com.taskadapter.connector.definition._
import com.taskadapter.connector.definition.exceptions.{CommunicationException, ConnectorException}
import com.taskadapter.connector.{FieldRow, NewConnector}
import com.taskadapter.core.PreviouslyCreatedTasksResolver
import com.taskadapter.model.{GTask, GUser}
import org.json.JSONException

object BasecampConnector {
  /**
    * Keep it the same to enable backward compatibility for previously created config files.
    */
  var ID = "Basecamp 2"
}

class BasecampConnector(config: BasecampConfig, setup: WebConnectorSetup, factory: ObjectAPIFactory) extends NewConnector {
  private val api = factory.createObjectAPI(config, setup)

  def loadData(): util.List[GTask] = {
    BasecampUtils.validateConfig(config)
    val api = factory.createObjectAPI(config, setup)
    val obj = api.getObject("projects/" + config.getProjectKey + "/todolists/" + config.getTodoKey + ".json")
    val todosobject = JsonUtils.getOptObject("todos", obj)
    if (todosobject == null) return new util.ArrayList[GTask]
    val completed = JsonUtils.getOptArray("completed", todosobject)
    val remaining = JsonUtils.getOptArray("remaining", todosobject)
    val res = new util.ArrayList[GTask](JsonUtils.genLen(completed) + JsonUtils.genLen(remaining))
    try {
      if (remaining != null) {
        for (i <- 0 until remaining.length) {
          val task = BasecampToGTask.parseTask(remaining.getJSONObject(i))
          task.setValue(BasecampField.doneRatio, 0)
          res.add(task)
        }
      }
      if (completed != null && config.getLoadCompletedTodos) {
        for (i <- 0 until completed.length) {
          val task = BasecampToGTask.parseTask(completed.getJSONObject(i))
          task.setValue(BasecampField.doneRatio, 100)
          res.add(task)
        }
      }
    } catch {
      case e: JSONException =>
        throw new CommunicationException(e)
    }
    res
  }

  @throws[ConnectorException]
  override def loadTaskByKey(id: TaskId, rows: java.lang.Iterable[FieldRow]): GTask = {
    BasecampUtils.validateConfig(config)
    val obj = api.getObject("projects/" + config.getProjectKey + "/todos/" + id.key + ".json")
    BasecampToGTask.parseTask(obj)
  }

  @throws[ConnectorException]
  def saveData(previouslyCreatedTasks: PreviouslyCreatedTasksResolver, tasks: util.List[GTask],
               monitor: ProgressMonitor,
               fieldRows: Iterable[FieldRow]): SaveResult = try {
    BasecampUtils.validateConfig(config)
    val userResolver = findUserResolver()
    val converter = new GTaskToBasecamp(userResolver)
    val saver = new BasecampSaver(api, config, userResolver)
    val resultBuilder = TaskSavingUtils.saveTasks(previouslyCreatedTasks, tasks, converter, saver, monitor, fieldRows,
      setup.host)

    resultBuilder.getResult
  }

  @throws[ConnectorException]
  private def findUserResolver(): UserResolver = {
    if (!config.isFindUserByName) return new DirectUserResolver
    val arr = api.getObjects("people.json")
    val users = new util.HashMap[String, GUser]
    for (i <- 0 until arr.length) {
      try {
        val user = BasecampToGTask.parseUser(arr.getJSONObject(i))
        users.put(user.getDisplayName, user)
      } catch {
        case e: JSONException =>
          throw new CommunicationException(e)
      }
    }
    new NamedUserResolver(users)
  }
}