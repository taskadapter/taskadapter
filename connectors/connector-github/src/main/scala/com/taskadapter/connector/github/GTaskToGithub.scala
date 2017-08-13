package com.taskadapter.connector.github

import java.io.IOException
import java.util
import java.util.Date

import com.google.common.base.Strings
import com.taskadapter.connector.common.data.ConnectorConverter
import com.taskadapter.connector.definition.exceptions.ConnectorException
import com.taskadapter.model.GTask
import org.eclipse.egit.github.core.{Issue, User}
import org.eclipse.egit.github.core.service.{IssueService, UserService}

class GTaskToGithub(userService: UserService) extends ConnectorConverter[GTask, Issue] {
  private val ghUsers = new util.HashMap[String, User]

  @throws[ConnectorException]
  def toIssue(task: GTask): Issue = {
    val issue = new Issue

    import scala.collection.JavaConversions._
    for (row <- task.getFields.entrySet) {
      processField(issue, row.getKey, row.getValue)
    }

    //    if (fieldsToExport.contains(GTaskDescriptor.FIELD.TASK_STATUS))
    //      issue.setState(if (task.getDoneRatio != null && (task.getDoneRatio eq 100)) IssueService.STATE_CLOSED
    //    else IssueService.STATE_OPEN)
    issue.setState(IssueService.STATE_OPEN)

    val key = task.getKey
    if (key != null) {
      val numericKey = key.toInt
      issue.setNumber(numericKey)
    }
    issue
  }

  private def processField(issue: Issue, fieldName: String, value: Any): Unit = {
    fieldName match {
      case GithubField.summary.name => issue.setTitle(value.asInstanceOf[String])
      case GithubField.description.name => issue.setBody(value.asInstanceOf[String])
      case GithubField.assignee.name => processAssignee(issue, fieldName, value)
      case GithubField.createdOn.name => issue.setCreatedAt(value.asInstanceOf[Date])
      case GithubField.updatedOn.name => issue.setUpdatedAt(value.asInstanceOf[Date])
      case _ => // unknown fields, ignore
    }
  }

  private def processAssignee(issue: Issue, fieldName: String, value: Any): Unit = {
    try {
      val userLogin = value.asInstanceOf[String]
      if (!Strings.isNullOrEmpty(userLogin)) {
        if (!ghUsers.containsKey(userLogin)) {
          val ghUser = userService.getUser(userLogin)
          ghUser.setName(ghUser.getLogin) // workaround for bug in eclipse-egit library - it uses name instead of login to build API request

          ghUsers.put(userLogin, ghUser)
        }
        if (ghUsers.get(userLogin) != null) issue.setAssignee(ghUsers.get(userLogin))
      }
    } catch {
      case e: IOException =>
        throw GithubUtils.convertException(e)
    }
  }

  @throws[ConnectorException]
  override def convert(source: GTask): Issue = toIssue(source)
}