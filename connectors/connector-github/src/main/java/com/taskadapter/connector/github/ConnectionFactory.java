package com.taskadapter.connector.github;

import com.taskadapter.connector.definition.WebServerInfo;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.service.UserService;

public class ConnectionFactory {
    private IssueService issueService;
    private RepositoryService repositoryService;
    private UserService userService;

    public ConnectionFactory(WebServerInfo serverInfo) {
        initServices(serverInfo);
    }

    private void initServices(WebServerInfo serverInfo) {
        GitHubClient ghClient = new GitHubClient();
        if (serverInfo.getUserName() != null
                && serverInfo.getUserName().trim().length() > 0
                && serverInfo.getPassword() != null
                && serverInfo.getPassword().trim().length() > 0) {
            ghClient.setCredentials(serverInfo.getUserName(), serverInfo.getPassword());
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
