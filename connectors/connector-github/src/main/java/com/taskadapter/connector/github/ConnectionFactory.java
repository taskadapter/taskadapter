package com.taskadapter.connector.github;

import com.taskadapter.connector.definition.WebConnectorSetup;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.service.UserService;

public class ConnectionFactory {
    private IssueService issueService;
    private RepositoryService repositoryService;
    private UserService userService;

    public ConnectionFactory(WebConnectorSetup setup) {
        initServices(setup);
    }

    private void initServices(WebConnectorSetup setup) {
        GitHubClient ghClient = new GitHubClient();
        if (setup.getUserName() != null
                && setup.getUserName().trim().length() > 0
                && setup.getApiKey() != null
                && setup.getApiKey().trim().length() > 0) {
            ghClient.setCredentials(setup.getUserName(), setup.getApiKey());
        }
        issueService = new IssueService(ghClient);
        repositoryService = new RepositoryService(ghClient);
        userService = new UserService(ghClient);
    }

    public RepositoryService getRepositoryService() {
        return repositoryService;
    }

    public IssueService getIssueService() {
        return issueService;
    }

    public UserService getUserService() {
        return userService;
    }
}
