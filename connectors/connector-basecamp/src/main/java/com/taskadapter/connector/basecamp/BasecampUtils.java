package com.taskadapter.connector.basecamp;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.taskadapter.connector.basecamp.beans.TodoList;
import com.taskadapter.connector.basecamp.exceptions.BadFieldException;
import com.taskadapter.connector.basecamp.exceptions.FieldNotSetException;
import com.taskadapter.connector.basecamp.transport.ObjectAPI;
import com.taskadapter.connector.basecamp.transport.ObjectAPIFactory;
import com.taskadapter.connector.definition.exceptions.CommunicationException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GProject;

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
}
