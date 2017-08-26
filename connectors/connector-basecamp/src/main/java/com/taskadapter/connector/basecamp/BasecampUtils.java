package com.taskadapter.connector.basecamp;

import com.taskadapter.connector.basecamp.beans.BasecampProject;
import com.taskadapter.connector.basecamp.beans.TodoList;
import com.taskadapter.connector.basecamp.exceptions.BadFieldException;
import com.taskadapter.connector.basecamp.exceptions.FieldNotSetException;
import com.taskadapter.connector.basecamp.transport.ObjectAPI;
import com.taskadapter.connector.basecamp.transport.ObjectAPIFactory;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.CommunicationException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class BasecampUtils {
    public static List<BasecampProject> loadProjects(ObjectAPIFactory factory,
                                                     BasecampConfig config, WebConnectorSetup setup)
            throws ConnectorException {
        final ObjectAPI objApi = factory.createObjectAPI(config, setup);
        final JSONArray objects = objApi.getObjects("projects.json");
        final List<BasecampProject> result = new ArrayList<>(
                objects.length());
        try {
            for (int i = 0; i < objects.length(); i++) {
                result.add(parseProjectFromList(objects.getJSONObject(i)));
            }
        } catch (JSONException e) {
            throw new CommunicationException("Bad content "
                    + objects.toString());
        }
        return result;
    }

    public static BasecampProject loadProject(ObjectAPIFactory factory,
                                              BasecampConfig config, WebConnectorSetup setup) throws ConnectorException {
        validateProject(config);
        validateAccount(config);
        final ObjectAPI objApi = factory.createObjectAPI(config, setup);
        String objectURL = "projects/" + config.getProjectKey() + ".json";
        final JSONObject object = objApi.getObject(objectURL);
        return parseFullProject(object);
    }

    // POST /projects/#{project_id}/todo_lists.xml
    static TodoList createTodoList(ObjectAPIFactory factory,
                                   BasecampConfig config, WebConnectorSetup setup,
                                   String todoListName,
                                   String todoListDescription) throws ConnectorException {
        String todoListJSonRepresentation = buildTodoListJSonObject(
                todoListName, todoListDescription);
        JSONObject result = factory.createObjectAPI(config, setup).post(
                "/projects/" + config.getProjectKey() + "/todolists.json",
                todoListJSonRepresentation);
        return parseTodoList(result);
    }

    static void deleteTodoList(ObjectAPIFactory factory,
                               BasecampConfig config, WebConnectorSetup setup) throws ConnectorException {
        factory.createObjectAPI(config, setup).delete(
                "/projects/" + config.getProjectKey() + "/todolists/"
                        + config.getTodoKey() + ".json");
    }

    private static String buildTodoListJSonObject(String todoListName,
                                                  String todoListDescription) {
        final StringWriter sw = new StringWriter();
        try {
            final JSONWriter writer = new JSONWriter(sw);
            writer.object();
            JsonUtils.writeOpt(writer, "name", todoListName);
            JsonUtils.writeOpt(writer, "description", todoListDescription);
            writer.endObject();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                sw.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return sw.toString();
    }

    public static List<TodoList> loadTodoLists(ObjectAPIFactory factory,
                                               BasecampConfig config, WebConnectorSetup setup) throws ConnectorException {
        validateProject(config);
        validateAccount(config);
        final ObjectAPI objApi = factory.createObjectAPI(config, setup);
        final JSONArray objects = objApi.getObjects("projects/"
                + config.getProjectKey() + "/todolists.json");
        final List<TodoList> result = new ArrayList<>(objects.length());
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

    public static TodoList loadTodoList(ObjectAPIFactory factory,
                                        BasecampConfig config,
                                        WebConnectorSetup setup) throws ConnectorException {
        validateConfig(config);
        final ObjectAPI objApi = factory.createObjectAPI(config, setup);
        final JSONObject object = objApi.getObject("projects/"
                + config.getProjectKey() + "/todolists/" + config.getTodoKey()
                + ".json");
        return parseTodoList(object);
    }

    public static void validateConfig(BasecampConfig config)
            throws ConnectorException {
        validateAccount(config);
        validateProject(config);
        validateTodolist(config);
    }

    public static void validateAccount(BasecampConfig config)
            throws ConnectorException {
        final String accountId = config.getAccountId();
        if (accountId == null || accountId.isEmpty()) {
            throw new FieldNotSetException("account-id");
        }
        if (!isNum(accountId)) {
            throw new BadFieldException("account-id");
        }
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
        res.setCompletedCount(JsonUtils.getInt("completed_count", jsonObject));
        res.setRemainingCount(JsonUtils.getInt("remaining_count", jsonObject));
        res.setName(JsonUtils.getOptString("name", jsonObject));
        return res;
    }

    private static BasecampProject parseProjectFromList(JSONObject jsonObject)
            throws CommunicationException {
        final BasecampProject project = new BasecampProject();
        project.setKey(Long.toString(JsonUtils.getLong("id", jsonObject)));
        project.setDescription(JsonUtils
                .getOptString("description", jsonObject));
        project.setName(JsonUtils.getOptString("name", jsonObject));
        return project;
    }

    private static BasecampProject parseFullProject(JSONObject jsonObject)
            throws CommunicationException {
        final BasecampProject project = new BasecampProject();
        project.setKey(Long.toString(JsonUtils.getLong("id", jsonObject)));
        project.setDescription(JsonUtils
                .getOptString("description", jsonObject));
        project.setName(JsonUtils.getOptString("name", jsonObject));
        JSONObject todolists;
        try {
            todolists = jsonObject.getJSONObject("todolists");
        } catch (JSONException e) {
            throw new CommunicationException(
                    "Can't parse todo lists object in the project: "
                            + e.toString());
        }
        project.setCompletedTodolists(JsonUtils.getInt("completed_count",
                todolists));
        project.setRemainingTodolists(JsonUtils.getInt("remaining_count",
                todolists));
        return project;
    }

}
