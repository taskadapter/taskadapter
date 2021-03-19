package com.taskadapter.connector.basecamp;

import com.taskadapter.connector.basecamp.transport.ObjectAPI;
import com.taskadapter.connector.common.BasicIssueSaveAPI;
import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.connector.definition.exceptions.ConnectorException;

public class BasecampSaver implements BasicIssueSaveAPI<BasecampTaskWrapper> {
    private ObjectAPI api;
    private BasecampConfig config;

    public BasecampSaver(ObjectAPI api, BasecampConfig config) {
        this.api = api;
        this.config = config;
    }

    /**
     * Creates a new task and returns a new task ID.
     */
    @Override
    public TaskId createTask(BasecampTaskWrapper wrapper) throws ConnectorException {
        var url = "/projects/" + config.getProjectKey() + "/todolists/" + config.getTodoKey() + "/todos.json";
        var res = api.post(url, wrapper.nativeTask());
        var newIdentity = BasecampToGTask.parseTask(res).getIdentity();
        /* Set "done ratio" if needed */
        if (wrapper.doneRatio() >= 100) {
            api.put("/projects/" + config.getProjectKey() + "/todos/" + newIdentity.getKey() + ".json",
                    wrapper.nativeTask());
        }
        return newIdentity;
    }

    /**
     * Updates an existing task.
     *
     * @param nativeTask native task representation.
     */
    @Override
    public void updateTask(BasecampTaskWrapper nativeTask) throws ConnectorException {
        var url = "/projects/" + config.getProjectKey() + "/todos/" + nativeTask.key() + ".json";
        var res = api.put(url, nativeTask.nativeTask());
        // BasecampToGTask.parseTask(res).getIdentity();
    }
}
