package com.taskadapter.connector.basecamp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.taskadapter.connector.basecamp.exceptions.CommunicationInterruptedException;
import com.taskadapter.connector.basecamp.exceptions.FatalMisunderstaningException;
import com.taskadapter.connector.basecamp.exceptions.ThrottlingException;
import com.taskadapter.connector.basecamp.transport.ObjectAPI;
import com.taskadapter.connector.basecamp.transport.ObjectAPIFactory;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.TaskSaveResult;
import com.taskadapter.connector.definition.TaskSaveResultBuilder;
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
                    final GTask task = BasecampUtils.parseTask(remaining
                            .getJSONObject(i));
                    task.setDoneRatio(Integer.valueOf(0));
                    res.add(task);
                }
            }
            if (completed != null) {
                for (int i = 0; i < completed.length(); i++) {
                    final GTask task = BasecampUtils.parseTask(completed
                            .getJSONObject(i));
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
        return BasecampUtils.parseTask(obj);
    }

    @Override
    public TaskSaveResult saveData(List<GTask> tasks, ProgressMonitor monitor,
            Mappings mappings) throws ConnectorException {
        BasecampUtils.validateConfig(config);
        final int total = countTasks(tasks);
        final UserResolver userResolver = new DirectUserResolver();
        final OutputContext ctx = new StandardOutputContext(mappings);
        final TaskSaveResultBuilder resultBuilder = new TaskSaveResultBuilder();
        monitor.beginTask("Saving...", total);
        writeTasks(tasks, monitor, userResolver, ctx, resultBuilder,
                factory.createObjectAPI(config), 0);
        return resultBuilder.getResult();
    }

    private int writeTasks(List<GTask> tasks, ProgressMonitor monitor,
            UserResolver userResolver, OutputContext ctx,
            TaskSaveResultBuilder resultBuilder, ObjectAPI api, int agg)
            throws ConnectorException {
        for (GTask task : tasks) {
            try {
                writeOneTask(task, userResolver, ctx, resultBuilder, api);
                monitor.worked(++agg);
            } catch (CommunicationInterruptedException e) {
                throw e;
            } catch (FatalMisunderstaningException e) {
                throw e;
            } catch (ThrottlingException e) {
                throw e;
            } catch (IOException e) {
                throw new ConnectorException("Internal connector exception", e);
            } catch (JSONException e) {
                throw new ConnectorException("Internal connector exception", e);
            } catch (ConnectorException ee) {
                resultBuilder.addTaskError(task, ee);
            }

            agg = writeTasks(task.getChildren(), monitor, userResolver, ctx,
                    resultBuilder, api, agg);
        }
        return agg;
    }

    private int countTasks(List<GTask> tasks) {
        if (tasks == null) {
            return 0;
        }
        int res = tasks.size();
        for (GTask task : tasks) {
            res += countTasks(task.getChildren());
        }
        return res;
    }

    private void writeOneTask(GTask task, UserResolver resolver,
            OutputContext ctx, TaskSaveResultBuilder resultBuilder,
            ObjectAPI api) throws ConnectorException, IOException,
            JSONException {
        final String repr = BasecampUtils.toRequest(task, resolver, ctx);
        final String remoteId = task.getRemoteId();
        if (remoteId == null) {
            final JSONObject res = api.post(
                    "/projects/" + config.getProjectKey() + "/todolists/"
                            + config.getTodoKey() + "/todos.json", repr);
            resultBuilder.addCreatedTask(task.getId(),
                    BasecampUtils.parseTask(res).getKey());
        } else {
            final JSONObject res = api.put(
                    "/projects/" + config.getProjectKey() + "/todos/"
                            + remoteId + ".json", repr);
            resultBuilder.addUpdatedTask(task.getId(),
                    BasecampUtils.parseTask(res).getKey());
        }
    }

    @Override
    public void updateRemoteIDs(Map<Integer, String> remoteIds,
            ProgressMonitor monitor, Mappings mappings)
            throws ConnectorException {
        throw new UnsupportedConnectorOperation("remote-ids");
    }

}
