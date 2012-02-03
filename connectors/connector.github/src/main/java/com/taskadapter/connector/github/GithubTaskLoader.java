package com.taskadapter.connector.github;

import com.taskadapter.connector.common.AbstractTaskLoader;
import com.taskadapter.connector.common.TaskConverter;
import com.taskadapter.model.GTask;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.service.IssueService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GithubTaskLoader load github issues
 */
public class GithubTaskLoader extends AbstractTaskLoader<GithubConfig> {

    public List<GTask> loadTasks(GithubConfig config) throws Exception {
        try {
            Map<String, String> issuesFilter = new HashMap<String, String>();
            issuesFilter.put(IssueService.FILTER_STATE,
                    config.getIssueState() == null ? IssueService.STATE_OPEN
                            : config.getIssueState());

            IssueService issueService = getIssueService(config);
            List<Issue> issues = issueService.getIssues(config.getServerInfo()
                    .getUserName(), config.getProjectKey(), issuesFilter);

            return getTaskConverter(config).convertToGenericTaskList(issues);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GTask loadTask(GithubConfig config, String taskKey) {
        try {
            IssueService issueService = new ConnectionFactory(config)
                    .getIssueService();

            Integer id = Integer.valueOf(taskKey);
            Issue issue = issueService.getIssue(config.getServerInfo()
                    .getUserName(), config.getProjectKey(), id);
            return getTaskConverter(config).convertToGenericTask(issue);
        } catch (Exception e) {
            throw new RuntimeException(e.toString(), e);
        }
    }

    private TaskConverter<Issue> getTaskConverter(GithubConfig config) {
        ConnectionFactory cf = new ConnectionFactory(config);
        return new GithubTaskConverter(cf.getUserService());
    }

    private IssueService getIssueService(GithubConfig config) {
        ConnectionFactory cf = new ConnectionFactory(config);
        return cf.getIssueService();
    }

}
