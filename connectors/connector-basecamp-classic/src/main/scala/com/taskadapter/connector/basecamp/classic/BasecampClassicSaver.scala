package com.taskadapter.connector.basecamp.classic

import com.taskadapter.connector.basecamp.{BasecampTaskWrapper, UserResolver}
import com.taskadapter.connector.basecamp.classic.transport.ObjectAPI
import com.taskadapter.connector.common.BasicIssueSaveAPI
import com.taskadapter.connector.definition.TaskId

class BasecampClassicSaver(api: ObjectAPI, config: BasecampClassicConfig, resolver: UserResolver)
  extends BasicIssueSaveAPI[BasecampTaskWrapper] {
  override def createTask(wrapper: BasecampTaskWrapper): TaskId = {
    val res = api.post("todo_lists/" + config.getTodoKey + "/todo_items.xml", wrapper.nativeTask)
    val newIdentity = BasecampClassicToGTask.parseTask(res).getIdentity
    if (wrapper.doneRatio >= 100) api.put("todo_items/" + newIdentity.key + "/complete.xml", "")
    newIdentity
  }

  override def updateTask(wrapper: BasecampTaskWrapper): Unit = {
    api.put("todo_items/" + wrapper.key + ".xml", wrapper.nativeTask)
    val res = api.getObject("todo_items/" + wrapper.key + ".xml")
    BasecampClassicToGTask.parseTask(res).getKey
  }
}
