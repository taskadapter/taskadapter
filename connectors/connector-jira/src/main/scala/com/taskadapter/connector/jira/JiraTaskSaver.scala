package com.taskadapter.connector.jira

import java.util

import com.atlassian.jira.rest.client.api.JiraRestClient
import com.atlassian.jira.rest.client.api.domain.input.{ComplexIssueInputFieldValue, FieldInput, IssueInput, LinkIssuesInput, TransitionInput}
import com.atlassian.jira.rest.client.api.domain.{Issue, IssueFieldId, IssueType}
import com.taskadapter.connector.common.{BasicIssueSaveAPI, RelationSaver}
import com.taskadapter.connector.definition.TaskId
import com.taskadapter.connector.definition.exceptions.ConnectorException
import com.taskadapter.model.{GRelation, Precedes}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

class JiraTaskSaver(client: JiraRestClient, issueTypeList: Iterable[IssueType],
                    defaultTaskTypeName: String,
                    defaultIssueTypeForSubtasks: String
                   ) extends RelationSaver with BasicIssueSaveAPI[IssueWrapper] {
  val logger = LoggerFactory.getLogger(classOf[JiraTaskSaver])

  @throws[ConnectorException]
  override def createTask(wrapper: IssueWrapper): TaskId = {
    val issueTypeName = JiraTaskTypeResolver.resolveIssueTypeNameForCreate(wrapper, defaultTaskTypeName, defaultIssueTypeForSubtasks)
    val issueTypeId = findIssueTypeId(issueTypeName)
    val issueWithTypeIdSet = getWithTaskTypeSet(wrapper, issueTypeId)

    val taskId = JiraClientHelper.createTask(client, issueWithTypeIdSet)

    // yes, reload the issue we just created. JIRA API is horrific
    val existingIssue = client.getIssueClient.getIssue(taskId.key).claim()
    updateStatusIfNeeded(existingIssue, wrapper.status)
    taskId
  }

  @throws[ConnectorException]
  override def updateTask(wrapper: IssueWrapper): Unit = {
    val existingIssue = client.getIssueClient.getIssue(wrapper.key).claim()

    val issueTypeId = if (wrapper.taskType.isDefined && !wrapper.taskType.get.isEmpty) {
      findIssueTypeId(wrapper.taskType.get)
    } else {
      existingIssue.getIssueType.getId.asInstanceOf[Long]
    }
    val issueWithTypeIdSet = getWithTaskTypeSet(wrapper, issueTypeId)
    client.getIssueClient.updateIssue(wrapper.key, issueWithTypeIdSet).claim

    updateStatusIfNeeded(existingIssue, wrapper.status)
  }

  def getWithTaskTypeSet(wrapper: IssueWrapper, issueTypeId: Long): IssueInput = {
    val newMap = wrapper.issueInput.getFields.asScala.filter(_._1 != "issuetype")
    newMap.put("issuetype", createType(issueTypeId))
    new IssueInput(newMap.asJava)
  }

  private def createType(issueTypeId: Long) =
    new FieldInput(IssueFieldId.ISSUE_TYPE_FIELD, ComplexIssueInputFieldValue.`with`("id", issueTypeId.toString))

  def updateStatusIfNeeded(issue: Issue, requiredStatus: String): Unit = {
    val oldStatus = issue.getStatus.getName
    if (requiredStatus != null && oldStatus != requiredStatus) {
      updateStatus(issue, requiredStatus)
    }
  }

  def updateStatus(existingIssue: Issue, newStatus: String) = {
    val transitions = client.getIssueClient.getTransitions(existingIssue).claim()
    val transitionInputMaybe = transitions.asScala.find(t=> t.getName == newStatus).map(t => new TransitionInput(t.getId))
    if (transitionInputMaybe.isDefined) {
      client.getIssueClient.transition(existingIssue, transitionInputMaybe.get).claim()
    }
  }

  @throws[ConnectorException]
  override def saveRelations(relations: util.List[GRelation]): Unit = {
    relations.asScala.foreach { relation =>
      val taskKey = relation.taskId.key
      val relatedTaskKey = relation.relatedTaskId.key

      if (relation.`type` == Precedes) {
        var linkTypeName = JiraConstants.getJiraLinkNameForPrecedes
        val input = new LinkIssuesInput(taskKey, relatedTaskKey, linkTypeName)
        val promise = client.getIssueClient.linkIssue(input)
        promise.claim
      } else {
        logger.info("Ignoring not supported issue link type: " + relation.`type` + ". JIRA connector only supports " + Precedes)
      }
    }
  }

  /**
    * Finds an issue type id to use.
    *
    * @return issue type id.
    */
  private def findIssueTypeId(taskType: String): Long = {
    val explicitTypeId = getIssueTypeIdByName(taskType)
    explicitTypeId
  }

  private def getIssueTypeIdByName(issueTypeName: String) = {
    issueTypeList.find(i => i.getName == issueTypeName).map(_.getId).orNull
  }
}
