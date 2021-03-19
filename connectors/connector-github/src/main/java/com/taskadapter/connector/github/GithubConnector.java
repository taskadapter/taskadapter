package com.taskadapter.connector.github;

import com.taskadapter.connector.FieldRow;
import com.taskadapter.connector.NewConnector;
import com.taskadapter.connector.common.TaskSavingUtils;
import com.taskadapter.connector.definition.ProgressMonitor;
import com.taskadapter.connector.definition.SaveResult;
import com.taskadapter.connector.definition.TaskId;
import com.taskadapter.connector.definition.WebConnectorSetup;
import com.taskadapter.connector.definition.exceptions.ConnectorException;
import com.taskadapter.core.PreviouslyCreatedTasksResolver;
import com.taskadapter.model.GTask;
import org.eclipse.egit.github.core.service.IssueService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GithubConnector implements NewConnector {
    private final GithubConfig config;
    private final WebConnectorSetup setup;

    /**
     * Keep it the same to enable backward compatibility with the existing
     * config files.
     */
    public static final String ID = "GitHub";

    public GithubConnector(GithubConfig config, WebConnectorSetup setup) {
        this.config = config;
        this.setup = setup;
    }

    public GTask loadTaskByKey(TaskId key, Iterable<FieldRow<?>> rows) throws ConnectorException {
        var issueService = new ConnectionFactory(setup).getIssueService();
        try {
            var issue = issueService.getIssue(setup.getUserName(), config.getProjectKey(), key.getId().intValue());
            return GithubToGTask.toGtask(issue);
        } catch (IOException e) {
            throw GithubUtils.convertException(e);
        }
    }

    private IssueService getIssueService() {
        var cf = new ConnectionFactory(setup);
        return cf.getIssueService();
    }

    @Override
    public List<GTask> loadData() throws ConnectorException {
        var issuesFilter = new HashMap<String, String>();
        issuesFilter.put(IssueService.FILTER_STATE,
                config.getIssueState() == null ? IssueService.STATE_OPEN
                        : config.getIssueState());

        var issueService = getIssueService();
        try {
            var issues = issueService.getIssues(setup.getUserName(),
                    config.getProjectKey(), issuesFilter);
            return GithubToGTask.toGTaskList(issues);
        } catch (IOException e) {
            throw GithubUtils.convertException(e);
        }
    }

    public SaveResult saveData(PreviouslyCreatedTasksResolver previouslyCreatedTasks, List<GTask> tasks,
                               ProgressMonitor monitor,
                               Iterable<FieldRow<?>> rows) {
        var ghConnector = new ConnectionFactory(setup);
        var converter = new GTaskToGithub(ghConnector.getUserService());
        var issueService = ghConnector.getIssueService();
        var saver = new GithubTaskSaver(issueService, setup.getUserName(), config.getProjectKey());
        var rb = TaskSavingUtils.saveTasks(previouslyCreatedTasks, tasks, converter, saver, monitor, rows,
                setup.getHost());
        return rb.getResult();
    }
}
