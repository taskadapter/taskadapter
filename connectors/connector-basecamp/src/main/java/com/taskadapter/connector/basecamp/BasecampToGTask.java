package com.taskadapter.connector.basecamp;

import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.connector.definition.exceptions.CommunicationException;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GUser;
import org.json.JSONObject;

public class BasecampToGTask {
    public static GTask parseTask(JSONObject obj) throws CommunicationException {
        var result = new GTask();
        var id = JsonUtils.getInt("id", obj);
        long longId = id;
        result.setId(longId);
        String stringId = id + "";
        result.setKey(stringId);
        // must set source system id, otherwise "update task" is impossible later
        result.setSourceSystemId(new TaskId(longId, stringId));

        result.setValue(BasecampField.content, JsonUtils.getOptString("content", obj));
        Float completedFloatValue = JsonUtils.getOptBool("completed", obj) ? 100f : 0f;
        result.setValue(AllFields.doneRatio, completedFloatValue);
        result.setValue(AllFields.dueDate, JsonUtils.getOptShortDate("due_at", obj));
        result.setValue(AllFields.createdOn, JsonUtils.getOptLongDate("created_at", obj));
        result.setValue(AllFields.updatedOn, JsonUtils.getOptLongDate("updated_at", obj));
        var assObj = JsonUtils.getOptObject("assignee", obj);
        if (assObj != null) {
            result.setValue(AllFields.assigneeFullName, parseUser(assObj).getDisplayName());
        }
        return result;
    }

    static GUser parseUser(JSONObject assObj) throws CommunicationException {
        return new GUser().setId(JsonUtils.getInt("id", assObj))
                .setDisplayName(JsonUtils.getOptString("name", assObj));
    }
}
