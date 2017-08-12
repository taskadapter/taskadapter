package com.taskadapter.connector.mantis

import biz.futureware.mantis.rpc.soap.client.AccountData
import biz.futureware.mantis.rpc.soap.client.IssueData
import biz.futureware.mantis.rpc.soap.client.ObjectRef
import biz.futureware.mantis.rpc.soap.client.ProjectData
import com.taskadapter.connector.common.data.ConnectorConverter
import com.taskadapter.connector.definition.exceptions.ConnectorException
import com.taskadapter.model.GTask
import com.taskadapter.model.GUser
import java.math.BigInteger
import java.util.{Calendar, Date}
import java.util

import com.google.common.base.Strings


object GTaskToMatis {
  val DEFAULT_TASK_DESCRIPTION = "-"
  /** see https://bitbucket.org/taskadapter/taskadapter/issues/25/once-created-tasks-cannot-be-updated-in
    * "Update task" fails unless you set some "category" on it. weirdly, "create tasks" works fine.
    * whatever, I will just set this "General" category that exists on a default MantisBT server.
    */
  val DEFAULT_TASK_CATEGORY = "General"
}

class GTaskToMatis(val mntProject: ProjectData, val users: util.List[AccountData]) extends ConnectorConverter[GTask, IssueData] {

  @throws[ConnectorException]
  override def convert(task: GTask): IssueData = {
    val issue = new IssueData
    val id = task.getId
    if (id != null) {
      issue.setId(BigInteger.valueOf(id))
    }

    import scala.collection.JavaConversions._
    for (row <- task.getFields.entrySet) {
      processField(issue, row.getKey, row.getValue)
    }

    // see Javadoc for DEFAULT_TASK_CATEGORY why need to set this.
    issue.setCategory(GTaskToMatis.DEFAULT_TASK_CATEGORY)
    val mntProjectRef = new ObjectRef(mntProject.getId, mntProject.getName)
    issue.setProject(mntProjectRef)
    issue
  }

  def processField(issue: IssueData, fieldName: String, value: Any): Unit = {
    fieldName match {
      case MantisField.summary.name => issue.setSummary(value.asInstanceOf[String])
      case MantisField.description.name =>
        // empty description is not allowed by Mantis API.
        // see bug https://www.hostedredmine.com/issues/39248
        if (Strings.isNullOrEmpty(value.asInstanceOf[String])) {
          issue.setDescription("-")
        } else {
          issue.setDescription(value.asInstanceOf[String])
        }
      case MantisField.dueDate.name =>
        if (value != null) {
          val calendar = Calendar.getInstance()
          calendar.setTime(value.asInstanceOf[Date])
          issue.setDue_date(calendar)
        }

      case MantisField.assignee.name =>
        if (value != null) {
          val ass = value.asInstanceOf[GUser]
          if (ass.getId != null) {
            val mntUser = new AccountData
            mntUser.setId(BigInteger.valueOf(ass.getId.asInstanceOf[Long]))
            mntUser.setName(ass.getLoginName)
            issue.setHandler(mntUser)
          } else {
            issue.setHandler(findUser(ass))
          }
        }

      case _ => // ignore the rest of the fields
    }
  }

  private def findUser(ass: GUser): AccountData = {
    if (users == null) return null
    // getting best name to search
    var nameToSearch = ass.getLoginName
    if (nameToSearch == null || "" == nameToSearch) nameToSearch = ass.getDisplayName
    if (nameToSearch == null || "" == nameToSearch) return null
    import scala.collection.JavaConversions._
    for (user <- users) {
      if (nameToSearch.equalsIgnoreCase(user.getName) || nameToSearch.equalsIgnoreCase(user.getReal_name)) return user
    }
    null
  }
}
