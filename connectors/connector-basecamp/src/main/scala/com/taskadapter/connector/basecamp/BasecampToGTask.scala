package com.taskadapter.connector.basecamp

import com.taskadapter.connector.definition.TaskId
import com.taskadapter.model._
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
    result.setValue(DoneRatio,
      if (JsonUtils.getOptBool("completed", obj)) 100f else 0f)
    result.setValue(DueDate, JsonUtils.getOptShortDate("due_at", obj))
    result.setValue(CreatedOn, JsonUtils.getOptLongDate("created_at", obj))
    result.setValue(UpdatedOn, JsonUtils.getOptLongDate("updated_at", obj))
    val assObj = JsonUtils.getOptObject("assignee", obj)
    if (assObj != null) result.setValue(Assignee, parseUser(assObj))
    result
  }

  def parseUser(assObj: JSONObject): GUser = {
    GUser(JsonUtils.getInt("id", assObj), null, JsonUtils.getOptString("name", assObj))
  }
}
