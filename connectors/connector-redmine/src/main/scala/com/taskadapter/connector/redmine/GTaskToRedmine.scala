package com.taskadapter.connector.redmine

import java.util
import java.util.Date

import com.google.common.base.Strings
import com.taskadapter.connector.common.ValueTypeResolver
import com.taskadapter.connector.common.data.ConnectorConverter
import com.taskadapter.connector.definition.exception.FieldConversionException
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
      val field = x._1
      val value = x._2
      try {
        processField(issue, field, value)
      } catch {
        case _: Exception => throw FieldConversionException(RedmineConnector.ID, field, value)
      }
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
      case AssigneeLoginName =>
        val maybeId = getUserIdByLogin(value.asInstanceOf[String])
        if (maybeId.isDefined) {
          issue.setAssigneeId(maybeId.get)
        }
      case AssigneeFullName =>
        val maybeId = getUserIdByFullName(value.asInstanceOf[String])
        if (maybeId.isDefined) {
          issue.setAssigneeId(maybeId.get)
        }
      case ReporterLoginName =>
        val maybeId = getUserIdByLogin(value.asInstanceOf[String])
        if (maybeId.isDefined) {
          issue.setAuthorId(maybeId.get)
        }
      case ReporterFullName =>
        val maybeId = getUserIdByFullName(value.asInstanceOf[String])
        if (maybeId.isDefined) {
          issue.setAuthorId(maybeId.get)
        }
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

  private def getUserIdByLogin(login: String): Option[Integer] = {
    usersCache.findRedmineUserByLogin(login).map(_.getId)
  }

  private def getUserIdByFullName(fullName: String): Option[Integer] = {
    usersCache.findRedmineUserByFullName(fullName).map(_.getId)
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


