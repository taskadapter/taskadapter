package com.taskadapter.connector.msp

import java.util

import com.taskadapter.connector.Field
import com.taskadapter.model._
import net.sf.mpxj.{ConstraintType, TaskField}

import scala.collection.JavaConverters._

object MspField {
  //  builder.addField(GTaskDescriptor.FIELD.TASK_TYPE, MSPUtils.getTextFieldNamesAvailableForMapping).withDefault(MSPUtils.getDefaultTaskType)
  //  builder.addField(GTaskDescriptor.FIELD.DUE_DATE, MSPUtils.getDueDateOptions).unselected.withDefault(MSPUtils.getDefaultDueDate)
  //  builder.addField(GTaskDescriptor.FIELD.REMOTE_ID, MSPUtils.getTextFieldNamesAvailableForMapping).unselected.withDefault(MSPUtils.getDefaultRemoteIdMapping)
  //  builder.addField(GTaskDescriptor.FIELD.ENVIRONMENT, MSPUtils.getTextFieldNamesAvailableForMapping).withDefault(MSPUtils.getDefaultFieldForEnvironment)
  //  builder.addField(GTaskDescriptor.FIELD.TARGET_VERSION, MSPUtils.getTextFieldNamesAvailableForMapping).withDefault(MSPUtils.getDefaultFieldForTargetVersion)

  val summary = Field(TaskField.SUMMARY.getName)
  val description = Field(TaskField.NOTES.getName)
  val assignee = Field(TaskField.ASSIGNMENT_OWNER.getName)
  val closedOn = Field.date(TaskField.ACTUAL_FINISH.getName)
  val priority = Field.integer(TaskField.PRIORITY.getName)
  val doneRatio = Field(TaskField.PERCENT_COMPLETE.getName)
  val taskDuration = Field(TaskField.DURATION.getName)
  val taskWork = Field(TaskField.WORK.getName)
  val status = Field(TaskField.TEXT24.getName)
  val taskType = Field(TaskField.TEXT23.getName)

  val startAsSoonAsPossible = Field(ConstraintType.AS_SOON_AS_POSSIBLE.name())
  val startAsLateAsPossible = Field(ConstraintType.AS_LATE_AS_POSSIBLE.name())
  val mustStartOn = Field.date(ConstraintType.MUST_START_ON.name())
  val mustFinishOn = Field.date(ConstraintType.MUST_FINISH_ON.name())
  val startNoEarlierThan = Field.date(ConstraintType.START_NO_EARLIER_THAN.name())
  val startNoLaterThan = Field.date(ConstraintType.START_NO_LATER_THAN.name())
  val finishNoEarlierThan = Field.date(ConstraintType.FINISH_NO_EARLIER_THAN.name())
  val finishNoLaterThan = Field.date(ConstraintType.FINISH_NO_LATER_THAN.name())

  val finish = Field.date(TaskField.FINISH.name())
  val deadline = Field.date(TaskField.DEADLINE.name())

  val fields = List(summary, description, assignee, closedOn, priority, doneRatio,
    taskDuration, taskWork, status, taskType,
    finish, deadline
    //    taskType, environment
  )

  def fieldsAsJava(): util.List[Field] = fields.asJava

  private def suggestedStandardFields = Map(summary -> Summary, description -> Description,
    //    taskType -> TaskType,
    // both fields are mapped to the same standard field because MSP supports both options
    taskDuration -> EstimatedTime,
    taskWork -> EstimatedTime,
    status -> TaskStatus,
    assignee -> Assignee,
    mustStartOn -> StartDate,
    closedOn -> ClosedOn,
    priority -> Priority,
    doneRatio -> DoneRatio,
    taskType -> TaskType,
    finish -> DueDate
  )

  def getSuggestedCombinations(): Map[Field, StandardField] = {
    suggestedStandardFields
  }
}
