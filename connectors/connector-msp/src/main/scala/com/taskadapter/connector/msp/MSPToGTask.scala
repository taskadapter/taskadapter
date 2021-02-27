package com.taskadapter.connector.msp

import com.taskadapter.connector.definition.TaskId
import com.taskadapter.model.{AssigneeFullName, CustomString, Description, GRelation, GRelationType, GTask, Summary}
import net.sf.mpxj.{ConstraintType, Duration, ProjectProperties, Relation, RelationType, Resource, Task, TimeUnit}

import java.util
import scala.collection.JavaConverters._

object MSPToGTask {
  /**
    * @return NULL if there's no assignee
    */
  private def getAssignee(task: Task): Resource = {
    val assignments = task.getResourceAssignments
    if ((assignments != null) && (assignments.size > 0)) { // just use the 1st one. see improvement request:
      // https://www.hostedredmine.com/issues/7772
      val ass = assignments.get(0)
      return ass.getResource
    }
    null
  }
}

class MSPToGTask private[msp](var projectProperties: ProjectProperties) {
  private[msp] def convertToGenericTaskList(tasks: util.List[Task]) = {
    tasks.asScala
      .filter(_.getName != null) // skip empty lines in MSP XML files
      .map(convertToGenericTask(_))
      .asJava
  }

  private def convertToGenericTask(task: Task) = {
    val genericTask = new GTask
    genericTask.setValue(Summary, task.getName)
    genericTask.setId(task.getUniqueID.longValue)
    genericTask.setKey(task.getUniqueID + "")
    genericTask.setSourceSystemId(new TaskId(task.getUniqueID.longValue(), task.getUniqueID + ""))
    val parent = task.getParentTask
    if (parent != null && (parent.getID != 0) && (parent.getUniqueID != 0)) {
      genericTask.setParentIdentity(new TaskId(parent.getUniqueID.longValue(), parent.getUniqueID + ""))
    }
    genericTask.setValue(MspField.priority, Integer.valueOf(task.getPriority.getValue))
    if (task.getWork != null) genericTask.setValue(MspField.taskWork, convertMspDurationToHours(task.getWork))
    if (task.getActualWork != null) genericTask.setValue(MspField.actualWork, convertMspDurationToHours(task.getActualWork))
    if (task.getDuration != null) genericTask.setValue(MspField.taskDuration, convertMspDurationToHours(task.getDuration))
    if (task.getActualDuration != null) genericTask.setValue(MspField.actualDuration, convertMspDurationToHours(task.getActualDuration))
    if (task.getPercentageComplete != null) genericTask.setValue(MspField.percentageComplete,
      java.lang.Float.valueOf(task.getPercentageComplete.floatValue()))
    // DATES
    val `type` = task.getConstraintType
    if (ConstraintType.START_NO_LATER_THAN == `type`) genericTask.setValue(MspField.startNoLaterThan, task.getConstraintDate)
    else if (ConstraintType.START_NO_EARLIER_THAN == `type`) genericTask.setValue(MspField.startNoEarlierThan, task.getConstraintDate)
    else if (ConstraintType.MUST_START_ON == `type`) genericTask.setValue(MspField.mustStartOn, task.getConstraintDate)
    else if (ConstraintType.AS_SOON_AS_POSSIBLE == `type`) genericTask.setValue(MspField.startAsSoonAsPossible, task.getStart)
    else if (ConstraintType.AS_LATE_AS_POSSIBLE == `type`) genericTask.setValue(MspField.startAsLateAsPossible, task.getStart)
    else if (ConstraintType.MUST_FINISH_ON == `type`) genericTask.setValue(MspField.mustFinishOn, task.getStart)
    genericTask.setValue(MspField.finish, task.getFinish)
    genericTask.setValue(MspField.deadline, task.getDeadline)
    genericTask.setValue(AssigneeFullName, extractAssignee(task))
    genericTask.setValue(Description, task.getNotes)
    for (i <- 1 to 30) {
      if (task.getText(i) != null) {
        genericTask.setValue(CustomString("Text" + i), task.getText(i))
      }
    }
    processRelations(task, genericTask)
    genericTask
  }

  private def processRelations(task: Task, genericTask: GTask) = {
    val relations = task.getSuccessors
    if (relations != null) relations.stream
      .filter((relation: Relation) => relation.getType == RelationType.FINISH_START)
      .forEach((relation: Relation) => {
        def foo(relation: Relation) = {
          val sourceTask = relation.getSourceTask
          val targetTask = relation.getTargetTask
          val r = new GRelation(
            new TaskId(sourceTask.getUniqueID.longValue(), sourceTask.getUniqueID + ""),
            new TaskId(targetTask.getUniqueID.longValue(), targetTask.getUniqueID + ""),
            GRelationType.precedes)
          genericTask.getRelations.add(r)
        }

        foo(relation)
      })
  }

  private def extractAssignee(task: Task): String = {
    val r = MSPToGTask.getAssignee(task)
    if (r != null) {
      /*
       * it only makes sense to use the ID if we know it came from us,
       * otherwise we'd try creating tasks in Redmine/Jira/.. using this
       * MSP-specific ID.
       */
      //Integer id = null;
      //if (r.getUniqueID() != null && MSPUtils.isResourceOurs(r)) {       //id = r.getUniqueID();       //}
      return r.getName
    }
    null
  }

  private def convertMspDurationToHours(mspDuration: Duration) = {
    val convertedToHoursDuration = mspDuration.convertUnits(TimeUnit.HOURS, projectProperties)
    convertedToHoursDuration.getDuration.toFloat
  }
}