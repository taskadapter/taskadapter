package com.taskadapter.connector.mantis

import java.math.BigInteger
import java.util
import java.util.{Calendar, Date}

import biz.futureware.mantis.rpc.soap.client.{AccountData, IssueData, ObjectRef, ProjectData}
import com.google.common.base.Strings
import com.taskadapter.connector.common.data.ConnectorConverter
import com.taskadapter.connector.definition.exception.FieldConversionException
import com.taskadapter.connector.definition.exceptions.ConnectorException
import com.taskadapter.model._

import scala.collection.JavaConverters._

object GTaskToMantis {
  val DEFAULT_TASK_DESCRIPTION = "-"
  /** see https://bitbucket.org/taskadapter/taskadapter/issues/25/once-created-tasks-cannot-be-updated-in
    * "Update task" fails unless you set some "category" on it. weirdly, "create tasks" works fine.
    * whatever, I will just set this "General" category that exists on a default MantisBT server.
    */
  val DEFAULT_TASK_CATEGORY = "General"
}

class GTaskToMantis(val mntProject: ProjectData, val users: util.List[AccountData]) extends ConnectorConverter[GTask, IssueData] {

  @throws[ConnectorException]
  override def convert(task: GTask): IssueData = {
    val issue = new IssueData
    val id = task.getId
    if (id != null) {
      issue.setId(BigInteger.valueOf(id))
    }

    for (row <- task.getFields.entrySet.asScala) {
      try {
        processField(issue, row.getKey, row.getValue)
      } catch {
        case e: Exception => throw new FieldConversionException(MantisConnector.ID, row.getKey, row.getValue, e.getMessage)
      }
    }

    // see Javadoc for DEFAULT_TASK_CATEGORY why need to set this.
    issue.setCategory(GTaskToMantis.DEFAULT_TASK_CATEGORY)
    val mntProjectRef = new ObjectRef(mntProject.getId, mntProject.getName)
    issue.setProject(mntProjectRef)
    issue
  }

  def processField(issue: IssueData, field: Field[_], value: Any): Unit = {
    field match {
      case _: Summary => issue.setSummary(value.asInstanceOf[String])
      case _: Description =>
        // empty description is not allowed by Mantis API.
        // see bug https://www.hostedredmine.com/issues/39248
        if (Strings.isNullOrEmpty(value.asInstanceOf[String])) {
          issue.setDescription("-")
        } else {
          issue.setDescription(value.asInstanceOf[String])
        }
      case _: DueDate =>
        if (value != null) {
          val calendar = Calendar.getInstance()
          calendar.setTime(value.asInstanceOf[Date])
          issue.setDue_date(calendar)
        }

      case _: AssigneeFullName =>
        val fullName = value.asInstanceOf[String]
        issue.setHandler(users.asScala.find(_.getName == fullName).orNull)
      case _: AssigneeLoginName =>
        val login = value.asInstanceOf[String]
        issue.setHandler(users.asScala.find(_.getName == login).orNull)
      case _ => // ignore the rest of the fields
    }
  }
}
