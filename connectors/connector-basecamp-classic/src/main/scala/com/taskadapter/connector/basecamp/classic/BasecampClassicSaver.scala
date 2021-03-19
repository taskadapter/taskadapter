package com.taskadapter.connector.basecamp.classic

import com.taskadapter.connector.basecamp.BasecampTaskWrapper
import com.taskadapter.connector.basecamp.classic.transport.ObjectAPI
import com.taskadapter.connector.common.BasicIssueSaveAPI
import com.taskadapter.connector.definition.TaskId

class BasecampClassicSaver(api: ObjectAPI, config: BasecampClassicConfig)
  extends BasicIssueSaveAPI[BasecampTaskWrapper] {
  override def createTask(wrapper: BasecampTaskWrapper): TaskId = {
    val res = api.post("todo_lists/" + config.getTodoKey + "/todo_items.xml", wrapper.getNativeTask)
    val newIdentity = BasecampClassicToGTask.parseTask(res).getIdentity
    if (wrapper.getDoneRatio >= 100) api.put("todo_items/" + newIdentity.getKey + "/complete.xml", "")
    newIdentity
  }

  override def updateTask(wrapper: BasecampTaskWrapper): Unit = {
    api.put("todo_items/" + wrapper.getKey + ".xml", wrapper.getNativeTask)
    val res = api.getObject("todo_items/" + wrapper.getKey + ".xml")
    BasecampClassicToGTask.parseTask(res).getKey
  }
}
