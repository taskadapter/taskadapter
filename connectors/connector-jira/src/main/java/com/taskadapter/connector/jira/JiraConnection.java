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

    public void deleteIssue(String issueKey, boolean deleteSubtasks) {
        restClient.getIssueClient().deleteIssue(issueKey, deleteSubtasks);
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

    public Iterable<BasicProject> getProjects() {
        return restClient.getProjectClient().getAllProjects(null);
    }

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

    public Iterable<Version> getVersions(String projectKey) {
        Project project = getProject(projectKey);
        if (project != null) {
            return project.getVersions();
        }
        else {
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
    // feature request for Jira REST API: https://jira.atlassian.com/browse/JRA-22306
    public RemoteFilter[] getSavedFilters() throws RemoteException {
        return jiraSoapService.getSavedFilters(authToken);
    }

    public User getUser(String userName) {
        return restClient.getUserClient().getUser(userName, null);
    }
}
