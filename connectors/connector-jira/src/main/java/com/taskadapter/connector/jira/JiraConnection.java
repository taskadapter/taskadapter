package com.taskadapter.connector.jira;

import com.atlassian.jira.rpc.soap.client.*;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class JiraConnection {
    private static final String JIRA_DUE_DATE_FORMAT = "d/MMM/yy";
    private JiraSoapService jiraSoapService;
    private String authToken;


    JiraConnection(JiraSoapService jiraSoapService, String authToken) {
        this.jiraSoapService = jiraSoapService;
        this.authToken = authToken;
    }

    public RemoteIssue[] getIssuesFromFilter(String queryId) throws RemoteException {
        return jiraSoapService.getIssuesFromFilter(authToken, queryId);
    }

    public RemoteIssue getIssueByKey(String issueKey) throws RemoteException {
        RemoteIssue[] issuesFromJqlSearch = jiraSoapService.getIssuesFromJqlSearch(authToken, "key=\"" + issueKey + "\"", 1);
        if (issuesFromJqlSearch.length == 0) {
            throw new RemoteException("Issue with ID not found");
        }
        return issuesFromJqlSearch[0];
    }

    public RemotePriority[] getPriorities() throws RemoteException {
        return jiraSoapService.getPriorities(authToken);
    }

    public RemoteIssueType[] getIssueTypeList(String projectName) throws RemoteException {
        return jiraSoapService.getIssueTypesForProject(authToken, getProject(projectName).getId());
    }

    public RemoteIssue[] get1IssueFromJqlSearch(String searchQuery) throws RemoteException {
        return jiraSoapService.getIssuesFromJqlSearch(authToken, searchQuery, 1);
    }

    public RemoteIssue createIssue(RemoteIssue rmIssueToCreate) throws RemoteException {
        return jiraSoapService.createIssue(authToken, rmIssueToCreate);
    }

    public RemoteIssue updateIssue(String issueKey, RemoteIssue rmIssueToUpdate) throws RemoteException {
        RemoteFieldValue[] fields = getFields(rmIssueToUpdate);
        return jiraSoapService.updateIssue(authToken, issueKey, fields);
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

    public RemoteProject[] getProjects() throws RemoteException {
        return jiraSoapService.getProjectsNoSchemes(authToken);
    }

    public RemoteProject getProject(String key) throws RemoteException {
        return jiraSoapService.getProjectByKey(authToken, key);
    }

    public RemoteVersion[] getVersions(String projectKey) throws RemoteException {
        return jiraSoapService.getVersions(authToken, projectKey);
    }

    public RemoteComponent[] getComponents(String projectKey) throws RemoteException {
        return jiraSoapService.getComponents(authToken, projectKey);
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
