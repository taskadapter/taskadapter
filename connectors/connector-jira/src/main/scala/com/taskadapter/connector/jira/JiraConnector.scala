package com.taskadapter.connector.jira

import java.io.IOException
import java.util

import com.atlassian.jira.rest.client.api.JiraRestClient
import com.atlassian.jira.rest.client.api.domain.IssueType
import com.taskadapter.connector.common.TaskSavingUtils
import com.taskadapter.connector.definition.exceptions.{BadConfigException, ConnectorException, ProjectNotSetException}
import com.taskadapter.connector.definition.{ProgressMonitor, SaveResult, TaskId, WebServerInfo}
import com.taskadapter.connector.{FieldRow, NewConnector}
import com.taskadapter.core.PreviouslyCreatedTasksResolver
import com.taskadapter.model.{GTask, NamedKeyedObject, NamedKeyedObjectImpl}
import org.slf4j.LoggerFactory


object JiraConnector {
  private val logger = LoggerFactory.getLogger(classOf[JiraConnector])
  /**
    * Keep it the same to enable backward compatibility with the existing
    * config files.
    */
  val ID = "Atlassian Jira"

  @FunctionalInterface private[jira] trait JiraRestClientAction[T] {
    @throws[IOException]
    @throws[ConnectorException]
    def apply(client: JiraRestClient): T
  }

}

class JiraConnector(val config: JiraConfig, var webServerInfo: WebServerInfo) extends NewConnector {
  // XXX refactor this. we don't even need the IDs!
  @throws[ConnectorException]
  def getFilters: util.List[NamedKeyedObject] = withJiraRestClient((client: JiraRestClient) => {
    def foo(client: JiraRestClient): util.List[NamedKeyedObject] = {
      // TODO need all filters, not just favorites - but JIRA REST API does not support. (Dec 6 2015)
      val filtersPromise = client.getSearchClient.getFavouriteFilters
      val filters = filtersPromise.claim
      val list = new util.ArrayList[NamedKeyedObject]
      import scala.collection.JavaConversions._
      for (filter <- filters) {
        list.add(new NamedKeyedObjectImpl(filter.getId + "", filter.getName))
      }
      list
    }

    foo(client)
  })

  @throws[ConnectorException]
  def getComponents: util.List[NamedKeyedObject] = withJiraRestClient((client: JiraRestClient) => {
    def foo(client: JiraRestClient) = {
      val projectKey = config.getProjectKey
      if (projectKey == null) throw new ProjectNotSetException
      val projectPromise = client.getProjectClient.getProject(projectKey)
      val project = projectPromise.claim
      val components = project.getComponents
      val list = new util.ArrayList[NamedKeyedObject]
      import scala.collection.JavaConversions._
      for (c <- components) {
        list.add(new NamedKeyedObjectImpl(String.valueOf(c.getId), c.getName))
      }
      list
    }

    foo(client)
  })

  @throws[ConnectorException]
  def getVersions: util.List[NamedKeyedObject] = withJiraRestClient((client: JiraRestClient) => {
    def foo(client: JiraRestClient) = {
      val projectKey = config.getProjectKey
      if (projectKey == null) throw new ProjectNotSetException
      val projectPromise = client.getProjectClient.getProject(projectKey)
      val project = projectPromise.claim
      val versions = project.getVersions
      val list = new util.ArrayList[NamedKeyedObject]
      import scala.collection.JavaConversions._
      for (version <- versions) {
        list.add(new NamedKeyedObjectImpl(String.valueOf(version.getId), version.getName))
      }
      list
    }

    foo(client)
  })

  @throws[ConnectorException]
  def getAllIssueTypes: util.List[NamedKeyedObject] = IssueTypesLoader.getIssueTypes(webServerInfo, new AllIssueTypesFilter)

  @throws[ConnectorException]
  def getIssueTypesForSubtasks: util.List[_ <: NamedKeyedObject] = IssueTypesLoader.getIssueTypes(webServerInfo, new SubtaskTypesFilter)

  override def loadTaskByKey(key: TaskId, rows: java.lang.Iterable[FieldRow]): GTask = withJiraRestClient((client: JiraRestClient) => {
    def foo(client: JiraRestClient) = {
      val loader = new JiraTaskLoader(client, config.getPriorities)
      loader.loadTask(key.key)
    }

    foo(client)
  })

  override def loadData: util.List[GTask] = withJiraRestClient((client: JiraRestClient) => {
    def foo(client: JiraRestClient) = {
      val loader = new JiraTaskLoader(client, config.getPriorities)
      loader.loadTasks(config)
    }

    foo(client)
  })

  override def saveData(previouslyCreatedTasks: PreviouslyCreatedTasksResolver, tasks: util.List[GTask], monitor: ProgressMonitor,
               rows: java.lang.Iterable[FieldRow]): SaveResult =
    withJiraRestClient((client: JiraRestClient) => {
    def foo(client: JiraRestClient) = {
      val issueTypeList = loadIssueTypes(client)
      val projectPromise = client.getProjectClient.getProject(config.getProjectKey)
      val project = projectPromise.claim
      val versions = project.getVersions
      val components = project.getComponents
      /* Need to load Jira server priorities because what we store in the config files is a
                   * priority name (string), while Jira returns the number value of the issue priority */ val prioritiesPromise = client.getMetadataClient.getPriorities
      val priorities = prioritiesPromise.claim
      val converter = new GTaskToJira(config, issueTypeList, versions, components, priorities)
      val saver = new JiraTaskSaver(client)
      val rb = TaskSavingUtils.saveTasks(previouslyCreatedTasks, tasks, converter, saver, monitor, rows)
      TaskSavingUtils.saveRemappedRelations(config, tasks, saver, rb)
      rb.getResult
    }

    foo(client)
  })

  @throws[BadConfigException]
  private def loadIssueTypes(jiraRestClient: JiraRestClient) : java.lang.Iterable[IssueType] = {
    val issueTypeListPromise = jiraRestClient.getMetadataClient.getIssueTypes
    val issueTypeList = issueTypeListPromise.claim
    //check if default issue type exists in Jira
    import scala.collection.JavaConversions._
    for (anIssueTypeList <- issueTypeList) {
      if (anIssueTypeList.getName == config.getDefaultTaskType) return issueTypeList
    }
    throw new BadConfigException("Default issue type " + config.getDefaultTaskType + " does not exist in JIRA")
  }

  private def withJiraRestClient[T](f: JiraConnector.JiraRestClientAction[T]) = try {
    val client = JiraConnectionFactory.createClient(webServerInfo)
    try
      f.apply(client)
    catch {
      case e: Exception =>
        //            throw JiraUtils.convertException(e);
        throw new RuntimeException(e)
    } finally if (client != null) client.close()
  }
}