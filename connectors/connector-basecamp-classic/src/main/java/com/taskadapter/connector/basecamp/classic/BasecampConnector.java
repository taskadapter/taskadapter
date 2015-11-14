package com.taskadapter.connector.basecamp.classic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import com.taskadapter.connector.basecamp.classic.exceptions.CommunicationInterruptedException;
import com.taskadapter.connector.basecamp.classic.exceptions.FatalMisunderstaningException;
import com.taskadapter.connector.basecamp.classic.exceptions.ThrottlingException;
import com.taskadapter.connector.basecamp.classic.transport.ObjectAPI;
import com.taskadapter.connector.basecamp.classic.transport.ObjectAPIFactory;
import com.taskadapter.connector.definition.Connector;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.TaskSaveResult;
import com.taskadapter.connector.definition.TaskSaveResultBuilder;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.connector.definition.exceptions.UnsupportedConnectorOperation;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GUser;
import com.taskadapter.model.GTaskDescriptor.FIELD;

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
        BasecampConfigValidator.validateServerAuth(config);
        BasecampConfigValidator.validateTodoList(config);
        final ObjectAPI api = factory.createObjectAPI(config);
        final Element obj = api.getObject("todo_lists/" + config.getTodoKey()
                + ".xml");
        final Element ilist = XmlUtils.getOptElt(obj, "todo-items");
        final List<Element> todosobject;
        if (ilist == null)
            todosobject = Collections.emptyList();
        else
            todosobject = XmlUtils.getDirectAncestors(ilist, "todo-item");
        final List<GTask> res = new ArrayList<>(todosobject.size());

        for (Element ee : todosobject) {
            res.add(BasecampUtils.parseTask(ee));
        }

        return res;
    }

    @Override
    public GTask loadTaskByKey(String key, Mappings mappings)
            throws ConnectorException {
        BasecampConfigValidator.validateServerAuth(config);
        final ObjectAPI api = factory.createObjectAPI(config);
        final Element obj = api.getObject("todo_items/" + key + ".xml");
        return BasecampUtils.parseTask(obj);
    }

    @Override
    public TaskSaveResult saveData(List<GTask> tasks, ProgressMonitor monitor,
            Mappings mappings) throws ConnectorException {
        BasecampConfigValidator.validateServerAuth(config);
        BasecampConfigValidator.validateTodoList(config);
        final int total = countTasks(tasks);
        final UserResolver userResolver = findUserResolver(mappings);
        final OutputContext ctx = new StandardOutputContext(mappings);
        final TaskSaveResultBuilder resultBuilder = new TaskSaveResultBuilder();
        monitor.beginTask("Saving...", total);
        writeTasks(tasks, monitor, userResolver, ctx, resultBuilder,
                factory.createObjectAPI(config), 0);
        return resultBuilder.getResult();
    }

    private UserResolver findUserResolver(Mappings mappings)
            throws ConnectorException {
        if (!mappings.isFieldSelected(FIELD.ASSIGNEE)) {
//                || !config.isLookupUsersByName()) {
            return new DirectUserResolver();
        }
        final ObjectAPI api = factory.createObjectAPI(config);
        final List<Element> arr = XmlUtils.getDirectAncestors(
                api.getObject("people.xml"), "person");
        final Map<String, GUser> users = new HashMap<>();
        for (Element eee : arr) {
            final GUser user = BasecampUtils.parseUser(eee);
            users.put(user.getDisplayName(), user);
        }
        return new NamedUserResolver(users);
    }

    private int writeTasks(List<GTask> tasks, ProgressMonitor monitor,
            UserResolver userResolver, OutputContext ctx,
            TaskSaveResultBuilder resultBuilder, ObjectAPI api, int agg)
            throws ConnectorException {
        for (GTask task : tasks) {
            try {
                writeOneTask(task, userResolver, ctx, resultBuilder, api);
                monitor.worked(++agg);
            } catch (CommunicationInterruptedException | FatalMisunderstaningException | ThrottlingException e) {
                throw e;
            } catch (IOException e) {
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
            ObjectAPI api) throws ConnectorException, IOException {
        final String todoItemXMLRepresentation = BasecampUtils.toRequest(task, resolver, ctx);
        final String remoteId = task.getRemoteId();
        if (remoteId == null) {
            final Element res = api.post("todo_lists/" + config.getTodoKey()
                    + "/todo_items.xml", todoItemXMLRepresentation);

            final String newTaskKey = BasecampUtils.parseTask(res).getKey();
             /* Set "done ratio" if needed */
            if (ctx.getXmlName(FIELD.DONE_RATIO) != null
                    && task.getDoneRatio() != null
                    && task.getDoneRatio() >= 100) {
                api.put("todo_items/" + newTaskKey + "/complete.xml","");
            }
            resultBuilder.addCreatedTask(task.getId(), newTaskKey);
        } else {
            api.put("todo_items/" + remoteId + ".xml", todoItemXMLRepresentation);
            final Element res = api
                    .getObject("todo_items/" + remoteId + ".xml");
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
