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
    result.setSourceSystemId(new TaskId(id.toLong, id.toString))

    result.setValue(BasecampField.content, JsonUtils.getOptString("content", obj))
    val completedFloatValue: java.lang.Float = if (JsonUtils.getOptBool("completed", obj)) 100f else 0f
    result.setValue(DoneRatio, completedFloatValue)
    result.setValue(DueDate, JsonUtils.getOptShortDate("due_at", obj))
    result.setValue(CreatedOn, JsonUtils.getOptLongDate("created_at", obj))
    result.setValue(UpdatedOn, JsonUtils.getOptLongDate("updated_at", obj))
    val assObj = JsonUtils.getOptObject("assignee", obj)
    if (assObj != null) result.setValue(AssigneeFullName, parseUser(assObj).getDisplayName)
    result
  }

  def parseUser(assObj: JSONObject): GUser = {
    new GUser().setId(JsonUtils.getInt("id", assObj))
      .setDisplayName(JsonUtils.getOptString("name", assObj))
  }
}
