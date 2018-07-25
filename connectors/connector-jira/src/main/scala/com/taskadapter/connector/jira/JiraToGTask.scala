package com.taskadapter.connector.jira

import java.util

import com.atlassian.jira.rest.client.api.domain.{BasicComponent, Issue, IssueLinkType}
import com.taskadapter.connector.Priorities
import com.taskadapter.connector.definition.TaskId
import com.taskadapter.model._
import org.codehaus.jettison.json.{JSONException, JSONObject}
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._
import scala.collection.JavaConverters

object JiraToGTask {
  private val logger = LoggerFactory.getLogger(classOf[JiraToGTask])

  def processParentTask(issue: Issue, task: GTask) = if (issue.getIssueType.isSubtask) {
    val parent = issue.getField("parent").getValue
    val json = parent.asInstanceOf[JSONObject]
    try {
      val parentKey = json.get("key").asInstanceOf[String]
      val id = json.getLong("id")
      task.setParentIdentity(new TaskId(id, parentKey))
    } catch {
      case e: JSONException =>
        e.printStackTrace()
    }
  }

  def processRelations(issue: Issue, genericTask: GTask) = {
    val links = issue.getIssueLinks
    if (links != null) {

      for (link <- links) {
        if (link.getIssueLinkType.getDirection == IssueLinkType.Direction.OUTBOUND) {
          val name = link.getIssueLinkType.getName
          if (name == JiraConstants.getJiraLinkNameForPrecedes) {
            // targetIssueIdFromURI = JiraUtils.getIdFromURI(link.getTargetIssueUri());
            val r = new GRelation(new TaskId(issue.getId, issue.getKey), new TaskId(-1, link.getTargetIssueKey), Precedes)
            genericTask.getRelations.add(r)
          }
          else logger.info("Relation type is not supported: " + link.getIssueLinkType + " - this link will be skipped for issue " + issue.getKey)
        }
      }
    }
  }
}

class JiraToGTask(val priorities: Priorities) {
  def convertToGenericTaskList(customFieldResolver: CustomFieldResolver, issues: Iterable[Issue]): util.List[GTask] = {
    // TODO see http://jira.atlassian.com/browse/JRA-6896
    // logger.info("Jira: no tasks hierarchy is supported");
    val rootLevelTasks = new util.ArrayList[GTask]
    for (issue <- issues) {
      val genericTask = convertToGenericTask(customFieldResolver, issue)
      rootLevelTasks.add(genericTask)
    }
    rootLevelTasks
  }

  def convertToGenericTask(customFieldResolver: CustomFieldResolver, issue: Issue): GTask = {
    val task = new GTask
    val longId = issue.getId
    task.setId(longId)
    task.setKey(issue.getKey)
    // must set source system id, otherwise "update task" is impossible later
    task.setSourceSystemId(new TaskId(longId, issue.getKey))
    val target = new util.ArrayList[String]
    issue.getComponents.forEach((c: BasicComponent) => target.add(c.getName))
    task.setValue(Components, JavaConverters.asScalaBuffer(target))
    if (issue.getAssignee != null) {
      val assignee = issue.getAssignee
      task.setValue(AssigneeLoginName, assignee.getName)
      task.setValue(AssigneeFullName, assignee.getDisplayName)
    }
    if (issue.getReporter != null) {
      task.setValue(ReporterFullName, issue.getReporter.getDisplayName)
      task.setValue(ReporterLoginName, issue.getReporter.getName)
    }
    task.setValue(TaskType, issue.getIssueType.getName)
    task.setValue(Summary, issue.getSummary)
    task.setValue(Description, issue.getDescription)
    task.setValue(TaskStatus, issue.getStatus.getName)
    val dueDate = issue.getDueDate
    if (dueDate != null) task.setValue(DueDate, dueDate.toDate)
    val createdOn = issue.getCreationDate
    if (createdOn != null) task.setValue(CreatedOn, createdOn.toDate)
    // TODO set Done Ratio
    // task.setDoneRatio(issue.getDoneRatio());
    val jiraPriorityName = if (issue.getPriority != null) {
      issue.getPriority.getName
    } else {
      null
    }
    val priorityValue = priorities.getPriorityByText(jiraPriorityName)
    task.setValue(Priority, priorityValue.asInstanceOf[Int])
    val timeTracking = issue.getTimeTracking
    if (timeTracking != null) {
      val originalEstimateMinutes = timeTracking.getOriginalEstimateMinutes
      if (originalEstimateMinutes != null && !(originalEstimateMinutes == 0)) task.setValue(EstimatedTime, (originalEstimateMinutes / 60.0).toFloat)

      val spentTimeMinutes = timeTracking.getTimeSpentMinutes
      task.setValue(SpentTime, (spentTimeMinutes/60.0).toFloat)
    }
    JiraToGTaskHelper.processCustomFields(customFieldResolver, issue, task)
    JiraToGTask.processRelations(issue, task)
    JiraToGTask.processParentTask(issue, task)
    task
  }
}