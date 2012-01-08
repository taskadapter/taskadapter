package com.taskadapter.connector.github;

import com.taskadapter.connector.common.AbstractTaskSaver;
import com.taskadapter.model.GRelation;
import com.taskadapter.model.GTask;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.service.IssueService;

import java.io.IOException;
import java.util.List;

public class GithubTaskSaver extends AbstractTaskSaver<GithubConfig> {

    private IssueService issueService;

        private GithubTaskConverter taskConverter;

        public GithubTaskSaver(GithubConfig config) {
            super(config);
            ConnectionFactory ghConnector = new ConnectionFactory(config);
            issueService = ghConnector.getIssueService();
            taskConverter = new GithubTaskConverter(ghConnector.getUserService());
        }


    @Override
    protected Issue convertToNativeTask(GTask task) {
    	return taskConverter.gtaskToIssue(task);
    }

    @Override
    protected GTask createTask(Object nativeTask) {
    	Issue issue = (Issue) nativeTask;
    	String userName = config.getServerInfo().getUserName();
    	String repositoryName = config.getProjectKey();
    	try {
			Issue createdIssue = issueService.createIssue(userName, repositoryName, issue);
	        return taskConverter.issueToGtask(createdIssue);
		} catch (IOException e) {
			throw new RuntimeException(e.toString(), e);
		}
    }

    @Override
    protected void updateTask(String taskId, Object nativeTask) {
        Issue issue = (Issue) nativeTask;
        try {
			issueService.editIssue(config.getServerInfo().getUserName(), config.getProjectKey(), issue);
		} catch (IOException e) {
			throw new RuntimeException(e.toString(), e);
		}
    }

    @Override
    protected void saveRelations(List<GRelation> relations) {
    	throw new RuntimeException("Method not implemented [saveRelations in GithubTaskSaver]");
    }

}
