package com.taskadapter.connector.basecamp

import com.taskadapter.connector.definition.TaskId
import com.taskadapter.model.{GTask, GUser}
import org.json.JSONObject

object BasecampToGTask {
  def parseTask(obj: JSONObject): GTask = {
    val result = new GTask
    val id = JsonUtils.getInt("id", obj)
    result.setId(id.toLong)
    result.setKey(id.toString)
    // must set source system id, otherwise "update task" is impossible later
    result.setSourceSystemId(TaskId(id.toLong, id.toString))

    result.setValue(BasecampField.content, JsonUtils.getOptString("content", obj))
    result.setValue(BasecampField.doneRatio,
      if (JsonUtils.getOptBool("completed", obj)) Integer.valueOf(100) else Integer.valueOf(0))
    result.setValue(BasecampField.dueDate, JsonUtils.getOptShortDate("due_at", obj))
    result.setValue(BasecampField.createdOn, JsonUtils.getOptLongDate("created_at", obj))
    result.setValue(BasecampField.updatedOn, JsonUtils.getOptLongDate("updated_at", obj))
    val assObj = JsonUtils.getOptObject("assignee", obj)
    if (assObj != null) result.setValue(BasecampField.assignee, parseUser(assObj))
    result
  }

  def parseUser(assObj: JSONObject): GUser = {
    val result = new GUser
    result.setId(JsonUtils.getInt("id", assObj))
    result.setDisplayName(JsonUtils.getOptString("name", assObj))
    result
  }
}
