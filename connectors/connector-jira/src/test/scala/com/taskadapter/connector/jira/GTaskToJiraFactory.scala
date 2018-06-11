package com.taskadapter.connector.jira

object GTaskToJiraFactory {

  val config = JiraPropertiesLoader.createTestConfig
  var priorities = MockData.loadPriorities
  val issueTypeList = MockData.loadIssueTypes
  val versions = MockData.loadVersions
  val components = MockData.loadComponents
  val customFieldsResolver = new CustomFieldResolver(Seq())

  def getConverter(): GTaskToJira = new GTaskToJira(config, customFieldsResolver, versions, components, priorities)

}
