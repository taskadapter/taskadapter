package com.taskadapter.connector.basecamp

import com.taskadapter.connector.basecamp.transport.ObjectAPI
import com.taskadapter.connector.common.BasicIssueSaveAPI
import com.taskadapter.connector.definition.TaskId

class BasecampSaver(api: ObjectAPI, config: BasecampConfig, resolver: UserResolver) extends BasicIssueSaveAPI[BasecampTaskWrapper] {
  /**
    * Creates a new task and returns a new task ID.
    */
  override def createTask(wrapper: BasecampTaskWrapper): TaskId = {
    val res = api.post("/projects/" + config.getProjectKey + "/todolists/" + config.getTodoKey + "/todos.json",
      wrapper.nativeTask)
    val newIdentity = BasecampToGTask.parseTask(res).getIdentity
    /* Set "done ratio" if needed */
    if (wrapper.doneRatio >= 100) {
      api.put("/projects/" + config.getProjectKey + "/todos/" + newIdentity.key + ".json", wrapper.nativeTask)
    }
    newIdentity
  }

  /**
    * Updates an existing task.
    *
    * @param nativeTask native task representation.
    */
  override def updateTask(nativeTask: BasecampTaskWrapper): Unit = {
    val res = api.put("/projects/" + config.getProjectKey + "/todos/" + nativeTask.key + ".json", nativeTask.nativeTask)
    BasecampToGTask.parseTask(res).getIdentity
  }
}
