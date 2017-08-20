package com.taskadapter.connector.redmine

import java.util
import java.util.Date

import com.google.common.base.Strings
import com.taskadapter.connector.common.data.ConnectorConverter
import com.taskadapter.connector.definition.TaskId
import com.taskadapter.model.{GTask, GUser}
import com.taskadapter.redmineapi.bean._
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

class GTaskToRedmine(config: RedmineConfig, priorities: util.Map[String, Integer], project: Project,
                     usersCache: RedmineUserCache, customFieldDefinitions: util.List[CustomFieldDefinition],
                     statusList: util.List[IssueStatus], versions: util.List[Version])
  extends ConnectorConverter[GTask, Issue] {
  val logger = LoggerFactory.getLogger(classOf[GTaskToRedmine])

  private def parseIntOrNull(s: String): Integer = {
    try
      s.toInt
    catch {
      case e@(_: NumberFormatException | _: NullPointerException) =>
        null
    }
  }

  private def convertToRedmineIssue(task: GTask): Issue = {
    val longId = task.getId
    val issue = longId match {
      case null => IssueFactory.create(null)
      case some => IssueFactory.create(some.intValue())
    }
    issue.setProjectId(project.getId)
    issue.setProjectName(project.getName)

    task.getFields.asScala.foreach { x =>
      processField(issue, x._1, x._2)
    }
    issue
  }

  private def processField(issue: Issue, fieldName: String, value: Any): Unit = {
    fieldName match {
      case "PARENT_KEY" => if (value != null) {
        issue.setParentId(value.asInstanceOf[TaskId].id.toInt)
      }
      case "ID" => // TODO TA3 review this. ignore ID field because it MAYBE does not need to be provided when saving?
      case "RELATIONS" => // processed in another place (for now?)
      case "CHILDREN" => // processed in another place (for now?)
      case "KEY" => // processed in [[DefaultValueSetter]] for now
      case "SOURCE_SYSTEM_ID" => // processed in [[DefaultValueSetter]] for now

      case RedmineField.summary.name => issue.setSubject(value.asInstanceOf[String])
      case RedmineField.startDate.name => issue.setStartDate(value.asInstanceOf[Date])
      case RedmineField.dueDate.name => issue.setDueDate(value.asInstanceOf[Date])
      case RedmineField.estimatedTime.name => issue.setEstimatedHours(value.asInstanceOf[Float])

      case RedmineField.doneRatio.name => issue.setDoneRatio(value.asInstanceOf[Float].toInt)
      case RedmineField.taskType.name =>
        var trackerName = value.asInstanceOf[String]
        if (Strings.isNullOrEmpty(trackerName)) trackerName = config.getDefaultTaskType
        issue.setTracker(project.getTrackerByName(trackerName))
      case RedmineField.taskStatus.name =>
        processTaskStatus(issue, value.asInstanceOf[String])
      case RedmineField.description.name =>
        issue.setDescription(value.asInstanceOf[String])
      case RedmineField.priority.name =>
        val priority = value.asInstanceOf[Integer]
        if (priority != null) {
          val priorityName = config.getPriorities.getPriorityByMSP(priority)
          val `val` = priorities.get(priorityName)
          if (`val` != null) {
            issue.setPriorityId(`val`)
            issue.setPriorityText(priorityName)
          }
        }
      case RedmineField.targetVersion.name =>
        val version = getVersionByName(value.asInstanceOf[String])
        issue.setTargetVersion(version)
      case RedmineField.createdOn.name => issue.setCreatedOn(value.asInstanceOf[Date])
      case RedmineField.updatedOn.name => issue.setUpdatedOn(value.asInstanceOf[Date])
      case RedmineField.assignee.name => processAssignee(issue, value)
      case RedmineField.author.name => processAuthor(issue, value)
      case _ =>
        // all known fields are processed. considering this a custom field
        val customFieldId = CustomFieldDefinitionFinder.findCustomFieldId(customFieldDefinitions, fieldName)
        if (customFieldId == null) throw new RuntimeException("Cannot find Id for custom field " + fieldName + ". Known fields are:" + customFieldDefinitions)
        val customField = CustomFieldFactory.create(customFieldId, fieldName, value.asInstanceOf[String])
        issue.addCustomField(customField)
    }
  }

  private def getVersionByName(versionName: String): Version = {
    if (versions == null || versionName == null) return null
    versions.asScala.find(_.getName == versionName).orNull
  }

  private def processAssignee(redmineIssue: Issue, value: Any): Unit = {
    val user = value.asInstanceOf[GUser]
    if (user != null) {
      val rmAss = usersCache.findRedmineUserInCache(user.getLoginName, user.getDisplayName)
      if (rmAss.isEmpty) {
        logger.warn(s"Converting task to Redmine format: assignee: cannot resolve user in Redmine for $user")
      } else {
        redmineIssue.setAssigneeId(rmAss.get.getId)
      }
    }
  }

  private def processAuthor(redmineIssue: Issue, value: Any): Unit = {
    val user = value.asInstanceOf[GUser]
    if (user != null) {
      val author = usersCache.findRedmineUserInCache(user.getLoginName, user.getDisplayName)
      if (author.isDefined) {
        redmineIssue.setAuthorId(author.get.getId)
      }
    }
  }

  private def processTaskStatus(issue: Issue, value: String): Unit = {
    var statusName = value
    if (statusName == null) statusName = config.getDefaultTaskStatus
    val status = getStatusByName(statusName)
    if (status != null) {
      issue.setStatusId(status.getId)
      issue.setStatusName(status.getName)
    }
  }

  /**
    * @return NULL if the status is not found or if "statusList" weren't previously set via setStatusList() method
    */
  private def getStatusByName(name: String): IssueStatus = {
    if (statusList == null || name == null) return null
    statusList.asScala.find(s => s.getName.equalsIgnoreCase(name)).orNull
  }

  override def convert(source: GTask): Issue = {
    convertToRedmineIssue(source)
  }
}


