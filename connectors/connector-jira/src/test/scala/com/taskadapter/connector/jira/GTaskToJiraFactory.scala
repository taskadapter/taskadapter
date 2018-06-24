package com.taskadapter.connector.jira

import com.atlassian.jira.rest.client.api.domain.Priority

object GTaskToJiraFactory {

  val config = JiraPropertiesLoader.createTestConfig
  var defaultPriorities = MockData.loadPriorities
  val issueTypeList = MockData.loadIssueTypes
  val versions = MockData.loadVersions
  val components = MockData.loadComponents
  val customFieldsResolver = new CustomFieldResolver(Seq())

  def getConverter(priorities: Iterable[Priority] = defaultPriorities): GTaskToJira =
    new GTaskToJira(config, customFieldsResolver, versions, components, priorities)

}
