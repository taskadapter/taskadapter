package com.taskadapter.connector.jira

import java.util

import com.atlassian.jira.rest.client.api.JiraRestClient
import com.atlassian.jira.rest.client.api.domain.{Issue, SearchResult}
import com.atlassian.jira.rest.client.api.domain.input.IssueInput
import com.taskadapter.connector.definition.TaskId
import scala.collection.JavaConverters._


/**
  * Layer on top of JIRA rest client library to help with paging.
  */
object JiraClientHelper {
  private val DEFAULT_PAGE_SIZE = 50
  private val ALL_FIELDS = Set("*all")

  /**
    * @return the new issue ID
    */
  def createTask(client: JiraRestClient, issueInput: IssueInput): TaskId = {
    val promise = client.getIssueClient.createIssue(issueInput)
    val createdIssue = promise.claim
    new TaskId(createdIssue.getId, createdIssue.getKey)
  }

  /**
    * Load all pages of search results.
    *
    * @param client pre-configured JIRA client
    * @param jql    Java Query Language string
    * @return all results found by the JQL (all pages)
    */
  def findIssues(client: JiraRestClient, jql: String): java.lang.Iterable[Issue] = findIssues(client, jql, DEFAULT_PAGE_SIZE)

  def findIssues(client: JiraRestClient, jql: String, pageSize: Int): java.lang.Iterable[Issue] = {
    val loadedIssues = new util.ArrayList[Issue]
    var searchResult: SearchResult = null
    do {
      val currentCursor = loadedIssues.size
      val searchPromise = client.getSearchClient.searchJql(jql, pageSize, currentCursor, ALL_FIELDS.asJava)
      searchResult = searchPromise.claim
      import scala.collection.JavaConversions._
      for (issue <- searchResult.getIssues) {
        loadedIssues.add(issue)
      }
    } while ( {
      loadedIssues.size < searchResult.getTotal
    })
    loadedIssues
  }

  def loadCustomFields(client: JiraRestClient): CustomFieldResolver = {
    val fields = client.getMetadataClient.getFields
    val fieldIterable = fields.claim.asScala
    new CustomFieldResolver(fieldIterable)
  }
}
