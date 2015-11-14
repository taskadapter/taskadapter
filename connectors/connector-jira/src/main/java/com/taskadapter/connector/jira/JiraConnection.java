package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.domain.*;
import com.atlassian.jira.rest.client.domain.input.IssueInput;
import com.atlassian.jira.rest.client.domain.input.LinkIssuesInput;
import com.atlassian.jira.rest.client.internal.json.CommonIssueJsonParser;
import com.atlassian.jira.rpc.soap.client.*;
import com.taskadapter.model.GRelation;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class JiraConnection {

    private static final int DEFAULT_PAGE_SIZE = 50;

    private JiraSoapService jiraSoapService;
    private String authToken;

    private JiraRestClient restClient;

    JiraConnection(JiraSoapService jiraSoapService, String authToken, JiraRestClient restClient) {
        this.jiraSoapService = jiraSoapService;
        this.authToken = authToken;
        this.restClient = restClient;
    }

    private List<Issue> searchJQL(String jql, final Integer maxPageSize) {
        int start = 0;
        final int pageSize = maxPageSize == null ? DEFAULT_PAGE_SIZE : maxPageSize.intValue();

        final List<Issue> result = new ArrayList<>();
        SearchResult<Issue> search;

        int section;

        do {
            section = 0;
            search = restClient.getSearchClient().searchJql(jql, pageSize, start,
                    "*all", null, new CommonIssueJsonParser());
            for (Issue i : search.getIssues()) {
                section++;
                result.add(i);
            }

            start += section;
        } while (result.size() < search.getTotal());
        return result;
    }

    public List<Issue> getIssuesFromFilter(String filterString, Integer pageSize) {
        return searchJQL(filterString, pageSize);
    }

    public List<Issue> getIssuesBySummary(String summary) {
        return getIssuesFromFilter("summary~\"" + summary + "\"", DEFAULT_PAGE_SIZE);
    }

    public List<Issue> getIssuesByQueryId(String projectKey, String queryId) {
        return searchJQL("project = " + projectKey + " AND filter = " + queryId, DEFAULT_PAGE_SIZE);

    }

    public List<Issue> getIssuesByProject(String projectKey) {
        return searchJQL("project = " + projectKey, DEFAULT_PAGE_SIZE);
    }

    public Issue getIssueByKey(String issueKey) {
        return restClient.getIssueClient().getIssue(issueKey, null);
    }

    public Iterable<Priority> getPriorities() {
        return restClient.getMetadataClient().getPriorities(null);
    }

    public Iterable<IssueType> getIssueTypeList() {
        return restClient.getMetadataClient().getIssueTypes(null);
    }

    public BasicIssue createIssue(IssueInput issueToCreate) {
        return restClient.getIssueClient().createIssue(issueToCreate, null);
    }

    public void updateIssue(String issueKey, IssueInput issueToUpdate) {
        restClient.getIssueClient().updateIssue(issueKey, issueToUpdate, null);
    }

    public Iterable<BasicProject> getProjects() {
        return restClient.getProjectClient().getAllProjects(null);
    }

    public Project getProject(String key) {
        return restClient.getProjectClient().getProject(key, null);
    }

    public void linkIssue(String issueKey, String targetIssueKey, GRelation.TYPE linkType) {
        String linkTypeName = null;
        // TODO http://www.hostedredmine.com/issues/99397 support localized issue link names
        if (linkType.equals(GRelation.TYPE.precedes)) {
            linkTypeName = JiraConstants.getJiraLinkNameForPrecedes();
        }

        if (linkTypeName != null) {
            restClient.getIssueClient()
                    .linkIssue(new LinkIssuesInput(issueKey, targetIssueKey, linkTypeName), null);
        }
    }

    public Iterable<Version> getVersions(String projectKey) {
        Project project = getProject(projectKey);
        if (project != null) {
            return project.getVersions();
        } else {
            return null;
        }
    }

    public Iterable<BasicComponent> getComponents(String projectKey) {
        Project project = getProject(projectKey);
        if (project != null) {
            return project.getComponents();
        } else {
            return null;
        }
    }

    // This requires Admin privileges.
    // see Jira bug https://jira.atlassian.com/browse/JRA-6857
    public RemoteField[] getCustomFields(String projectKey) throws RemoteException {
        return jiraSoapService.getCustomFields(authToken);
    }

    // this is not available in Jira REST API at this moment (Sep 3, 2012).
    // see http://docs.atlassian.com/jira/REST/latest/#id127412
    // feature request for Jira REST API:
    // https://jira.atlassian.com/browse/JRA-22306
    public RemoteFilter[] getSavedFilters() throws RemoteException {
        return jiraSoapService.getSavedFilters(authToken);
    }

    public User getUser(String userName) {
        return restClient.getUserClient().getUser(userName, null);
    }
}
