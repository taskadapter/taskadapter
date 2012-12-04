package com.taskadapter.connector.basecamp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.taskadapter.connector.basecamp.transport.ObjectAPI;
import com.taskadapter.connector.basecamp.transport.ObjectAPIFactory;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.TaskSaveResult;
import com.taskadapter.connector.definition.exceptions.CommunicationException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.UnsupportedConnectorOperation;
import com.taskadapter.model.GTask;

final class BasecampConnector implements Connector<BasecampConfig> {

    private final BasecampConfig config;
    private final ObjectAPIFactory factory;

    BasecampConnector(BasecampConfig config, ObjectAPIFactory factory) {
        super();
        this.config = config;
        this.factory = factory;
    }

    @Override
    public List<GTask> loadData(Mappings mappings, ProgressMonitor monitor)
            throws ConnectorException {
        BasecampUtils.validateConfig(config);
        final ObjectAPI api = factory.createObjectAPI(config);
        final JSONObject obj = api.getObject("projects/"
                + config.getProjectKey() + "/todolists/" + config.getTodoKey()
                + ".json");
        final JSONObject todosobject = JsonUtils.getOptObject("todos", obj);
        if (todosobject == null) {
            return new ArrayList<GTask>();
        }
        final JSONArray completed = JsonUtils.getOptArray("completed",
                todosobject);
        final JSONArray remaining = JsonUtils.getOptArray("remaining",
                todosobject);
        final List<GTask> res = new ArrayList<GTask>(
                JsonUtils.genLen(completed) + JsonUtils.genLen(remaining));

        try {
            if (remaining != null) {
                for (int i = 0; i < remaining.length(); i++) {
                    final GTask task = BasecampUtils.parseTask(
                            remaining.getJSONObject(i), mappings);
                    task.setDoneRatio(Integer.valueOf(0));
                    res.add(task);
                }
            }
            if (completed != null) {
                for (int i = 0; i < completed.length(); i++) {
                    final GTask task = BasecampUtils.parseTask(
                            completed.getJSONObject(i), mappings);
                    task.setDoneRatio(Integer.valueOf(100));
                    res.add(task);
                }
            }
        } catch (JSONException e) {
            throw new CommunicationException(e);
        }
        return res;
    }

    @Override
    public GTask loadTaskByKey(String key, Mappings mappings)
            throws ConnectorException {
        BasecampUtils.validateConfig(config);
        final ObjectAPI api = factory.createObjectAPI(config);
        final JSONObject obj = api.getObject("projects/"
                + config.getProjectKey() + "/todos/" + key + ".json");
        return BasecampUtils.parseTask(obj, mappings);
    }

    @Override
    public TaskSaveResult saveData(List<GTask> tasks, ProgressMonitor monitor,
            Mappings mappings) throws ConnectorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateRemoteIDs(Map<Integer, String> remoteIds,
            ProgressMonitor monitor, Mappings mappings)
            throws ConnectorException {
        throw new UnsupportedConnectorOperation("remote-ids");
    }

}
