package com.taskadapter.connector.msp

import java.util

import com.taskadapter.connector.Field
import com.taskadapter.model._
import net.sf.mpxj.{ConstraintType, TaskField}

import scala.collection.JavaConverters._

object MspField {

  val summary = Field(TaskField.SUMMARY.getName)
  val description = Field(TaskField.NOTES.getName)
  val assignee = Field.user(TaskField.ASSIGNMENT_OWNER.getName)
  val closedOn = Field.date(TaskField.ACTUAL_FINISH.getName)
  val priority = Field.integer(TaskField.PRIORITY.getName)
  val percentageComplete = Field.integer(TaskField.PERCENT_COMPLETE.getName)
  val taskDuration = Field.float(TaskField.DURATION.getName)
  val taskWork = Field.float(TaskField.WORK.getName)
  val status = Field(TaskField.TEXT24.getName)
  val taskType = Field(TaskField.TEXT23.getName)
  val actualWork = Field.integer(TaskField.ACTUAL_WORK.getName)
  val actualDuration = Field.integer(TaskField.ACTUAL_DURATION.getName)
  val actualFinish = Field.date(TaskField.ACTUAL_FINISH.getName)

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

  val fields = List(actualDuration, actualWork, actualFinish,
    summary, description, assignee, closedOn, priority, percentageComplete,
    taskDuration, taskWork, status, taskType,
    finish, deadline
  )

  def fieldsAsJava(): util.List[Field] = fields.asJava

  private def suggestedStandardFields = Map(
    summary -> Summary, description -> Description,
    taskDuration -> EstimatedTime,
    status -> TaskStatus,
    assignee -> Assignee,
    mustStartOn -> StartDate,
    closedOn -> ClosedOn,
    priority -> Priority,
    percentageComplete -> DoneRatio,
    taskType -> TaskType,
    finish -> DueDate
  )

  def getSuggestedCombinations(): Map[Field, StandardField] = {
    suggestedStandardFields
  }
}
