package com.taskadapter.connector.redmine;

import com.taskadapter.connector.common.AbstractTaskSaver;
import com.taskadapter.connector.common.RelationSaver;
import com.taskadapter.connector.definition.Mappings;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.exceptions.CommunicationException;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GTask;
import com.taskadapter.model.GTaskDescriptor.FIELD;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineProcessingException;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssuePriority;
import com.taskadapter.redmineapi.bean.IssueStatus;
import com.taskadapter.redmineapi.bean.Project;
import com.taskadapter.redmineapi.bean.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedmineTaskSaver extends AbstractTaskSaver<RedmineConfig, Issue> implements RelationSaver {

    private final RedmineManager mgr;
    private final Project rmProject;
    private final GTaskToRedmine converter;
    private final RedmineToGTask toGTask;

    public RedmineTaskSaver(RedmineConfig config, Mappings mappings, ProgressMonitor monitor)
            throws ConnectorException {
        super(config, monitor);
        this.mgr = RedmineManagerFactory.createRedmineManager(config
                .getServerInfo());
        this.rmProject = loadProject();
        this.converter = new GTaskToRedmine(config, mappings,
                loadPriorities(mappings), rmProject, loadUsers(),
                loadStatusList());
        toGTask = new RedmineToGTask(config);
    }

    private Project loadProject() throws ConnectorException {
        try {
            return mgr.getProjectByKey(config.getProjectKey());
        } catch (RedmineException e) {
            throw RedmineExceptions.convertException(e);
        }
    }

    private Map<String, Integer> loadPriorities(final Mappings mappings)
            throws ConnectorException {
        if (!mappings.isFieldSelected(FIELD.PRIORITY)) {
            return new HashMap<String, Integer>();
        }
        final Map<String, Integer> res = new HashMap<String, Integer>();
        try {
            for (IssuePriority prio : mgr.getIssuePriorities()) {
                res.put(prio.getName(), prio.getId());
            }
        } catch (RedmineException e) {
            throw RedmineExceptions.convertException(e);
        }
        return res;
    }

    private List<User> loadUsers() throws ConnectorException {
        if (!config.isFindUserByName()) {
            return new ArrayList<User>();
        }
        
        try {
            return mgr.getUsers();
        } catch (RedmineException e) {
            throw RedmineExceptions.convertException(e);
        }
    }

    private List<IssueStatus> loadStatusList() throws ConnectorException {
        List<IssueStatus> statusList;

        try {
            statusList = mgr.getStatuses();
        } catch (RedmineException e) {
            throw RedmineExceptions.convertException(e);
        }

        return statusList;
    }

    @Override
    protected Issue convertToNativeTask(GTask task) {
        return converter.convertToRedmineIssue(task);
    }

    @Override
    protected GTask createTask(Issue nativeTask) throws ConnectorException {
        try {
            Issue newIssue = mgr.createIssue(rmProject.getIdentifier(),
                    (Issue) nativeTask);
            return toGTask.convertToGenericTask(newIssue);
        } catch (RedmineException e) {
            throw RedmineExceptions.convertException(e);
        }
    }

    @Override
    protected void updateTask(String taskId, Issue nativeTask) throws ConnectorException {
        Issue rmIssue = (Issue) nativeTask;
        rmIssue.setId(Integer.parseInt(taskId));
        try {
            mgr.update(rmIssue);

            if (config.getSaveIssueRelations()) {
                mgr.deleteIssueRelationsByIssueId(rmIssue.getId());
            }
        } catch (RedmineException e) {
            throw RedmineExceptions.convertException(e);
        }
    }

    @Override
    public void saveRelations(List<GRelation> relations) throws ConnectorException {
        try {
            for (GRelation gRelation : relations) {
                int taskKey = Integer.parseInt(gRelation.getTaskKey());
                int relatedTaskKey = Integer.parseInt(gRelation
                        .getRelatedTaskKey());
                mgr.createRelation(taskKey, relatedTaskKey, gRelation.getType().toString());
            }
        } catch (RedmineProcessingException e) {
            throw new RelationCreationException(e);
        } catch (RedmineException e) {
            throw new CommunicationException(e);
        }
    }

}
