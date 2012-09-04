package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.NullProgressMonitor;
import com.atlassian.jira.rest.client.domain.*;
import com.atlassian.jira.rest.client.domain.input.IssueInput;
import com.atlassian.jira.rest.client.domain.input.LinkIssuesInput;
import com.atlassian.jira.rest.client.internal.json.CommonIssueJsonParser;
import com.atlassian.jira.rest.client.internal.json.IssueJsonParser;
import com.atlassian.jira.rest.client.internal.json.JsonParseUtil;
import com.atlassian.jira.rpc.soap.client.*;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.taskadapter.model.GRelation;

import javax.naming.directory.SearchControls;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

public class JiraConnection {
    private static final String JIRA_DUE_DATE_FORMAT = "d/MMM/yy";

    private JiraSoapService jiraSoapService;
    private String authToken;

    private JiraRestClient restClient;

    JiraConnection(JiraSoapService jiraSoapService, String authToken, JiraRestClient restClient) {
        this.jiraSoapService = jiraSoapService;
        this.authToken = authToken;
        this.restClient = restClient;
    }

/*    public RemoteIssue[] getIssuesFromFilter(String queryId) throws RemoteException {
        return jiraSoapService.getIssuesFromFilter(authToken, queryId);
    }*/

    public List<Issue> getIssuesFromFilter(String filterString) {
        SearchResult<Issue> result = restClient.getSearchClient().searchJql(filterString, "\u002A"+"all", null, new CommonIssueJsonParser());
        return Lists.newArrayList(result.getIssues());
    }

    public List<Issue> getIssuesBySummary(String summary) {
        return getIssuesFromFilter("summary~\"" + summary + "\"");
    }

    public Iterable<Issue> getIssuesByProject(String projectKey) {
        SearchResult<Issue> result = restClient.getSearchClient().searchJql("project = " + projectKey, "\u002A"+"all", null, new CommonIssueJsonParser());

        return result.getIssues();
    }

/*    public RemoteIssue getIssueByKey(String issueKey) throws RemoteException {
        RemoteIssue[] issuesFromJqlSearch = jiraSoapService.getIssuesFromJqlSearch(authToken, "key=\"" + issueKey + "\"", 1);
        if (issuesFromJqlSearch.length == 0) {
            throw new RemoteException("Issue with ID not found");
        }
        return issuesFromJqlSearch[0];
    }*/

    public Issue getIssueByKey(String issueKey) {
        return restClient.getIssueClient().getIssue(issueKey, null);
    }

/*    public RemotePriority[] getPriorities() throws RemoteException {
        return jiraSoapService.getPriorities(authToken);
    }*/

    public Iterable<Priority> getPriorities() {
        return restClient.getMetadataClient().getPriorities(null);
    }

/*    public RemoteIssueType[] getIssueTypeList(String projectName) throws RemoteException {
        return jiraSoapService.getIssueTypesForProject(authToken, getProjectOld(projectName).getId());
    }*/

    public Iterable<IssueType> getIssueTypeList() {
        return restClient.getMetadataClient().getIssueTypes(null);
    }

/*    public RemoteIssue[] get1IssueFromJqlSearch(String searchQuery) throws RemoteException {
        return jiraSoapService.getIssuesFromJqlSearch(authToken, searchQuery, 1);
    }*/

/*    public Issue createIssue(Issue rmIssueToCreate) throws RemoteException {
        return jiraSoapService.createIssue(authToken, null);
    }*/

    public BasicIssue createIssue(IssueInput issueToCreate) {
        return restClient.getIssueClient().createIssue(issueToCreate, null);
    }

    public void updateIssue(String issueKey, IssueInput issueToUpdate) {
        restClient.getIssueClient().updateIssue(issueKey, issueToUpdate, null);
    }

/*    public void updateIssue(Issue issueToUpdate) {
        //restClient.getIssueClient().updateIssue(issueToUpdate);
    }*/

    public void deleteIssue(String issueKey, boolean deleteSubtasks) {
        restClient.getIssueClient().deleteIssue(issueKey, deleteSubtasks);
    }

    public void deleteIssue(Issue issueToDelete, boolean deleteSubtasks) {
        restClient.getIssueClient().deleteIssue(issueToDelete, deleteSubtasks);
    }

    // XXX maybe there could a method in the API for this?
    // need to build and check the latest API from here:
    // https://svn.atlassian.com/svn/public/atlassian/rpc-jira-plugin/tags/atlassian_jira_4_2_2/jira-soapclient/
    // the old one (which I used to generate Jira API) is here:
    // https://svn.atlassian.com/svn/public/atlassian/rpc-jira-plugin/tags/atlassian_jira_4_1_1_1/jira-soapclient
    private RemoteFieldValue[] getFields(RemoteIssue rmIssueToUpdate) {
        RemoteFieldValue[] fields = new RemoteFieldValue[4];
        fields[0] = new RemoteFieldValue("summary", new String[]{rmIssueToUpdate.getSummary()});
        fields[1] = new RemoteFieldValue("description", new String[]{rmIssueToUpdate.getDescription()});
        fields[2] = new RemoteFieldValue("assignee", new String[]{rmIssueToUpdate.getAssignee()});
        String dueDateString = getDateString(rmIssueToUpdate.getDuedate());
        fields[3] = new RemoteFieldValue("duedate", new String[]{dueDateString});
        return fields;
    }

    private static String getDateString(Calendar c) {
        if (c == null) {
            return "";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(JIRA_DUE_DATE_FORMAT);
        return formatter.format(c.getTime());
    }

/*    public RemoteProject[] getProjectsOld() throws RemoteException {
        return jiraSoapService.getProjectsNoSchemes(authToken);
    }*/

    public Iterable<BasicProject> getProjects() {
        return restClient.getProjectClient().getAllProjects(null);
    }

/*    public RemoteProject getProjectOld(String key) throws RemoteException {
        return jiraSoapService.getProjectByKey(authToken, key);
    }*/

    public Project getProject(String key) {
        return restClient.getProjectClient().getProject(key, null);
    }

    public void linkIssue(String issueKey, String targetIssueKey, GRelation.TYPE linkType) {
        String linkTypeName = null;
        // TODO http://www.hostedredmine.com/issues/99397
        if (linkType.equals(GRelation.TYPE.precedes)) {
            linkTypeName = JiraConstants.getJiraLinkNameForPrecedes();
        }

        if (linkTypeName != null) {
            restClient.getIssueClient().linkIssue(new LinkIssuesInput(issueKey, targetIssueKey, linkTypeName), null);
        }
    }

    public Iterable<Version> getVersions(String projectKey) throws RemoteException {
        Project project = getProject(projectKey);
        if (project != null) {
            return project.getVersions();
        }
        else {
            return null;
        }
    }

    public Iterable<BasicComponent> getComponents(String projectKey) throws RemoteException {
        Project project = getProject(projectKey);
        if (project != null) {
            return project.getComponents();
        }
        else {
            return null;
        }
    }

    // This requires Admin privileges.
    // see Jira bug https://jira.atlassian.com/browse/JRA-6857
    public RemoteField[] getCustomFields(String projectKey) throws RemoteException {
        return jiraSoapService.getCustomFields(authToken);
    }

    public RemoteFilter[] getSavedFilters() throws RemoteException {
        return jiraSoapService.getSavedFilters(authToken);
    }

    public RemoteUser getUser(String userName) throws RemoteException {
        return jiraSoapService.getUser(authToken, userName);
    }
}
