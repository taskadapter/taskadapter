package com.taskadapter.connector.basecamp;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.NewConnector;
import com.taskadapter.connector.basecamp.transport.ObjectAPI;
import com.taskadapter.connector.basecamp.transport.ObjectAPIFactory;
import com.taskadapter.connector.common.TaskSavingUtils;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.SaveResult;
import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.CommunicationException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.core.PreviouslyCreatedTasksResolver;
import com.taskadapter.model.AllFields;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GUser;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class BasecampConnector implements NewConnector {
    /**
     * Keep it the same to enable backward compatibility for previously created config files.
     */
    public static final String ID = "Basecamp 2";

    private final BasecampConfig config;
    private final WebConnectorSetup setup;
    private final ObjectAPIFactory factory;
    private final ObjectAPI api;

    public BasecampConnector(BasecampConfig config, WebConnectorSetup setup, ObjectAPIFactory factory) {
        this.config = config;
        this.setup = setup;
        this.factory = factory;
        try {
            api = factory.createObjectAPI(config, setup);
        } catch (ConnectorException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<GTask> loadData() throws ConnectorException {
        BasecampValidator.validateConfigWithException(config);
        var api = factory.createObjectAPI(config, setup);
        var obj = api.getObject("projects/" + config.getProjectKey()
                + "/todolists/" + config.getTodoKey() + ".json");
        var todosobject = JsonUtils.getOptObject("todos", obj);
        if (todosobject == null) {
            return new ArrayList<>();
        }
        var completed = JsonUtils.getOptArray("completed", todosobject);
        var remaining = JsonUtils.getOptArray("remaining", todosobject);
        var res = new ArrayList<GTask>(JsonUtils.genLen(completed) + JsonUtils.genLen(remaining));
        try {
            if (remaining != null) {
                for (int i = 0; i < remaining.length(); i++) {
                    var task = BasecampToGTask.parseTask(remaining.getJSONObject(i));
                    task.setValue(AllFields.doneRatio, (float) 0);
                    res.add(task);
                }
            }
            if (completed != null && config.getLoadCompletedTodos()) {
                for (int i = 0; i < completed.length(); i++) {
                    var task = BasecampToGTask.parseTask(completed.getJSONObject(i));
                    task.setValue(AllFields.doneRatio, 100f);
                    res.add(task);
                }
            }
        } catch (JSONException e) {
            throw new CommunicationException(e);
        }
        return res;
    }

    @Override
    public GTask loadTaskByKey(TaskId id, Iterable<FieldRow<?>> rows) throws ConnectorException {
        BasecampValidator.validateConfigWithException(config);
        var obj = api.getObject("projects/" + config.getProjectKey() + "/todos/" + id.getKey() + ".json");
        return BasecampToGTask.parseTask(obj);
    }

    @Override
    public SaveResult saveData(PreviouslyCreatedTasksResolver previouslyCreatedTasks,
                               List<GTask> tasks,
                               ProgressMonitor monitor,
                               Iterable<FieldRow<?>> fieldRows) {
        try {
            BasecampValidator.validateConfigWithException(config);
            var users = loadUsers();
            var converter = new GTaskToBasecamp(users);
            var saver = new BasecampSaver(api, config);
            var resultBuilder = TaskSavingUtils.saveTasks(previouslyCreatedTasks, tasks, converter, saver, monitor, fieldRows,
                    setup.getHost());

            return resultBuilder.getResult();
        } catch (ConnectorException e) {
            return SaveResult.withError(e);
        }
    }

    private List<GUser> loadUsers() throws ConnectorException {
        if (config.isFindUserByName()) {
            var arr = api.getObjects("people.json");
            var users = new ArrayList<GUser>();
            for (int i = 0; i < arr.length(); i++) {
                try {
                    var user = BasecampToGTask.parseUser(arr.getJSONObject(i));
                    users.add(user);
                } catch (JSONException e) {
                    throw new CommunicationException(e);
                }
            }
            return users;
        } else {
            return List.of();
        }
    }
}
