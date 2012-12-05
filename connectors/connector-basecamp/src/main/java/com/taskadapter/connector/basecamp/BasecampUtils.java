package com.taskadapter.connector.basecamp;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;

import com.taskadapter.connector.basecamp.beans.TodoList;
import com.taskadapter.connector.basecamp.exceptions.BadFieldException;
import com.taskadapter.connector.basecamp.exceptions.FieldNotSetException;
import com.taskadapter.connector.basecamp.transport.ObjectAPI;
import com.taskadapter.connector.basecamp.transport.ObjectAPIFactory;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.exceptions.CommunicationException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GProject;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor.FIELD;
import com.taskadapter.model.GUser;

public class BasecampUtils {
    public static List<GProject> loadProjects(ObjectAPIFactory factory,
            BasecampConfig config) throws ConnectorException {
        final ObjectAPI objApi = factory.createObjectAPI(config);
        final JSONArray objects = objApi.getObjects("projects.json");
        final List<GProject> result = new ArrayList<GProject>(objects.length());
        try {
            for (int i = 0; i < objects.length(); i++) {
                result.add(parseProject(objects.getJSONObject(i)));
            }
        } catch (JSONException e) {
            throw new CommunicationException("Bad content "
                    + objects.toString());
        }
        return result;
    }

    public static List<TodoList> loadTodoLists(ObjectAPIFactory factory,
            BasecampConfig config) throws ConnectorException {
        validateProject(config);
        final ObjectAPI objApi = factory.createObjectAPI(config);
        final JSONArray objects = objApi.getObjects("projects/"
                + config.getProjectKey() + "/todolists.json");
        final List<TodoList> result = new ArrayList<TodoList>(objects.length());
        try {
            for (int i = 0; i < objects.length(); i++) {
                result.add(parseTodoList(objects.getJSONObject(i)));
            }
        } catch (JSONException e) {
            throw new CommunicationException("Bad content "
                    + objects.toString());
        }
        return result;
    }

    public static void validateConfig(BasecampConfig config)
            throws ConnectorException {
        validateProject(config);
        validateTodolist(config);
    }

    public static void validateProject(BasecampConfig config)
            throws ConnectorException {
        final String projectKey = config.getProjectKey();
        if (projectKey == null) {
            throw new FieldNotSetException("project-key");
        }
        if (!isNum(projectKey)) {
            throw new BadFieldException("project-key");
        }
    }

    public static void validateTodolist(BasecampConfig config)
            throws ConnectorException {
        final String listKey = config.getTodoKey();
        if (listKey == null) {
            throw new FieldNotSetException("todo-key");
        }
        if (!isNum(listKey)) {
            throw new BadFieldException("todo-key");
        }
    }

    private static boolean isNum(String str) {
        if (str.length() == 0) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            final char chr = str.charAt(i);
            if (chr < '0' || '9' < chr) {
                return false;
            }
        }
        return true;
    }

    private static TodoList parseTodoList(JSONObject jsonObject)
            throws CommunicationException {
        final TodoList res = new TodoList();
        res.setKey(Long.toString(JsonUtils.getLong("id", jsonObject)));
        res.setDescription(JsonUtils.getOptString("description", jsonObject));
        res.setUrl(JsonUtils.getOptString("homepage", jsonObject));
        res.setName(JsonUtils.getOptString("name", jsonObject));
        return res;
    }

    private static GProject parseProject(JSONObject jsonObject)
            throws CommunicationException {
        final GProject res = new GProject();
        res.setKey(Long.toString(JsonUtils.getLong("id", jsonObject)));
        res.setDescription(JsonUtils.getOptString("description", jsonObject));
        res.setHomepage(JsonUtils.getOptString("homepage", jsonObject));
        res.setName(JsonUtils.getOptString("name", jsonObject));
        return res;
    }

    public static GTask parseTask(JSONObject obj)
            throws ConnectorException {
        final GTask result = new GTask();
        result.setId(JsonUtils.getInt("id", obj));
        result.setKey(Long.toString(JsonUtils.getLong("id", obj)));
        result.setDescription(JsonUtils.getOptString("content", obj));
        result.setSummary(JsonUtils.getOptString("content", obj));
        result.setDoneRatio(JsonUtils.getOptBool("completed", obj) ? Integer
                .valueOf(100) : Integer.valueOf(0));
        result.setDueDate(JsonUtils.getOptShortDate("due_at", obj));
        result.setCreatedOn(JsonUtils.getOptLongDate("created_at", obj));
        result.setUpdatedOn(JsonUtils.getOptLongDate("updated_at", obj));
        final JSONObject assObj = JsonUtils.getOptObject("assignee", obj);
        if (assObj != null) {
            result.setAssignee(parseUser(assObj));
        }
        return result;
    }

    public static GUser parseUser(JSONObject assObj)
            throws CommunicationException {
        final GUser result = new GUser();
        result.setId(JsonUtils.getInt("id", assObj));
        result.setDisplayName(JsonUtils.getOptString("name", assObj));
        return result;
    }

    public static String toRequest(GTask task, UserResolver users,
            OutputContext ctx) throws IOException, JSONException,
            ConnectorException {
        final StringWriter sw = new StringWriter();
        try {
            final JSONWriter writer = new JSONWriter(sw);
            writer.object();
            JsonUtils.writeOpt(writer, ctx.getJsonName(FIELD.DESCRIPTION),
                    task.getDescription());
            JsonUtils.writeOpt(writer, ctx.getJsonName(FIELD.SUMMARY),
                    task.getSummary());
            JsonUtils.writeOpt(writer, ctx.getJsonName(FIELD.DONE_RATIO), task
                    .getDoneRatio() == null ? null : task.getDoneRatio()
                    .intValue() >= 100 ? Boolean.TRUE : Boolean.FALSE);
            JsonUtils.writeShort(writer, ctx.getJsonName(FIELD.DUE_DATE),
                    task.getDueDate());
            writeAssignee(writer, ctx, users, task.getAssignee());
            writer.endObject();
        } finally {
            sw.close();
        }
        return sw.toString();
    }

    private static void writeAssignee(JSONWriter writer, OutputContext ctx,
            UserResolver resolver, GUser assignee) throws JSONException,
            ConnectorException {
        final String field = ctx.getJsonName(FIELD.ASSIGNEE);
        if (field == null) {
            return;
        }
        if (assignee == null) {
            writer.key(field).value(null);
            return;
        }
        assignee = resolver.resolveUser(assignee);
        if (assignee == null || assignee.getId() == null) {
            return;
        }

        writer.key(field).object().key("type").value("Person");
        writer.key("id").value(assignee.getId().intValue());
        writer.endObject();
    }
}
