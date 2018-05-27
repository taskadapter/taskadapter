package com.taskadapter.connector.redmine

import java.util
import java.util.Date

import com.google.common.base.Strings
import com.taskadapter.connector.common.ValueTypeResolver
import com.taskadapter.connector.common.data.ConnectorConverter
import com.taskadapter.model._
import com.taskadapter.redmineapi.bean._
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

class GTaskToRedmine(config: RedmineConfig, priorities: util.Map[String, Integer], project: Project,
                     usersCache: RedmineUserCache, customFieldDefinitions: util.List[CustomFieldDefinition],
                     statusList: util.List[IssueStatus], versions: util.List[Version],
                     categories: util.List[IssueCategory])
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
    if (task.getParentIdentity != null) {
      issue.setParentId(task.getParentIdentity.id.toInt)
    }
    task.getFields.asScala.foreach { x =>
      processField(issue, x._1, x._2)
    }
    issue
  }

  private def processField(issue: Issue, field: Field[_], value: Any): Unit = {
    field match {
      case Id => // ignore ID field because it does not need to be provided when saving
      case ParentKey => // processed above
      case Relations => // processed in another place (for now?)
      case Children => // processed in another place (for now?)
      case Key => // processed in [[DefaultValueSetter]] for now
      case SourceSystemId => // processed in [[DefaultValueSetter]] for now

      case RedmineField.category =>
        val categoryName = ValueTypeResolver.getValueAsString(value)
        val maybeCategory = getCategoryByName(categoryName)
        issue.setCategory(maybeCategory.orNull)
      case Summary => issue.setSubject(value.asInstanceOf[String])
      case StartDate => issue.setStartDate(value.asInstanceOf[Date])
      case DueDate => issue.setDueDate(value.asInstanceOf[Date])
      case EstimatedTime =>
        issue.setEstimatedHours(ValueTypeResolver.getValueAsFloat(value))

      case DoneRatio => issue.setDoneRatio(ValueTypeResolver.getValueAsInt(value))
      case TaskType =>
        var trackerName = value.asInstanceOf[String]
        if (Strings.isNullOrEmpty(trackerName)) trackerName = config.getDefaultTaskType
        issue.setTracker(project.getTrackerByName(trackerName))
      case TaskStatus =>
        processTaskStatus(issue, value.asInstanceOf[String])
      case Description =>
        issue.setDescription(value.asInstanceOf[String])
      case Priority =>
        val priority = value.asInstanceOf[Integer]
        if (priority != null) {
          val priorityName = config.getPriorities.getPriorityByMSP(priority)
          val `val` = priorities.get(priorityName)
          if (`val` != null) {
            issue.setPriorityId(`val`)
            issue.setPriorityText(priorityName)
          }
        }
      case TargetVersion =>
        val version = getVersionByName(value.asInstanceOf[String])
        issue.setTargetVersion(version)
      case CreatedOn => issue.setCreatedOn(value.asInstanceOf[Date])
      case UpdatedOn => issue.setUpdatedOn(value.asInstanceOf[Date])
      case AssigneeLoginName => processAssigneeByLogin(issue, value)
      case AssigneeFullName => processAssigneeFullName(issue, value)
      case Reporter => processAuthor(issue, value)
      case _ =>
        // all known fields are processed. considering this a custom field
        val customFieldId = CustomFieldDefinitionFinder.findCustomFieldId(customFieldDefinitions, field)
        if (customFieldId == null) throw new RuntimeException("Cannot find Id for custom field " + field + ". Known fields are:" + customFieldDefinitions)
        val customField = CustomFieldFactory.create(customFieldId, field.name, value.asInstanceOf[String])
        issue.addCustomField(customField)
    }
  }

  private def getVersionByName(versionName: String): Version = {
    if (versions == null || versionName == null) return null
    versions.asScala.find(_.getName == versionName).orNull
  }

  private def getCategoryByName(name: String): Option[IssueCategory] = {
    if (categories == null || name == null) return None
    categories.asScala.find(_.getName == name)
  }

  private def processAssigneeByLogin(redmineIssue: Issue, value: Any): Unit = {
    val user = value.asInstanceOf[String]
    if (user != null) {
      val rmAss = usersCache.findRedmineUserByLogin(user)
      if (rmAss.isEmpty) {
        logger.warn(s"Converting task to Redmine format: assignee: cannot resolve user in Redmine for $user")
      } else {
        redmineIssue.setAssigneeId(rmAss.get.getId)
      }
    }
  }
  private def processAssigneeFullName(redmineIssue: Issue, value: Any): Unit = {
    val user = value.asInstanceOf[String]
    if (user != null) {
      val rmAss = usersCache.findRedmineUserByFullName(user)
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
      val author = usersCache.findRedmineUserInCache(user.loginName, user.displayName)
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


