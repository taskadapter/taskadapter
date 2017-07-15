package com.taskadapter.connector.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.util.concurrent.Promise;
import com.taskadapter.connector.definition.TaskId;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Layer on top of JIRA rest client library to help with paging.
 */
final class JiraClientHelper {

    private static final int DEFAULT_PAGE_SIZE = 50;
    private static final Set<String> ALL_FIELDS = null;

    /**
     * @return the new issue ID
     */
    public static TaskId createTask(JiraRestClient client, IssueInput issueInput) {
        final Promise<BasicIssue> promise = client.getIssueClient().createIssue(issueInput);
        final BasicIssue createdIssue = promise.claim();
        return new TaskId(createdIssue.getId(), createdIssue.getKey());
    }

    /**
     * Load all pages of search results.
     *
     * @param client pre-configured JIRA client
     * @param jql    Java Query Language string
     * @return all results found by the JQL (all pages)
     */
    public static Iterable<Issue> findIssues(JiraRestClient client, String jql) {
        return findIssues(client, jql, DEFAULT_PAGE_SIZE);
    }

    public static Iterable<Issue> findIssues(JiraRestClient client, String jql, int pageSize) {
        final List<Issue> loadedIssues = new ArrayList<>();
        SearchResult searchResult;
        do {
            int currentCursor = loadedIssues.size();
            Promise<SearchResult> searchPromise = client.getSearchClient().searchJql(jql, pageSize, currentCursor, ALL_FIELDS);
            searchResult = searchPromise.claim();
            for (Issue issue : searchResult.getIssues()) {
                loadedIssues.add(issue);
            }
        } while (loadedIssues.size() < searchResult.getTotal());
        return loadedIssues;
    }
}
