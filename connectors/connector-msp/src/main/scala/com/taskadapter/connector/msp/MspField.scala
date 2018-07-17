package com.taskadapter.connector.msp

import com.taskadapter.model._
import net.sf.mpxj.{ConstraintType, TaskField}

object MspField {

  val closedOn = CustomDate(TaskField.ACTUAL_FINISH.getName)
  val priority = Priority
  val percentageComplete = DoneRatio
  val taskDuration = CustomFloat(TaskField.DURATION.getName)
  val taskWork = CustomFloat(TaskField.WORK.getName)
  val status = Field(TaskField.TEXT24.getName)
  val taskType = Field(TaskField.TEXT23.getName)
  val actualWork = CustomFloat(TaskField.ACTUAL_WORK.getName)
  val actualDuration = CustomFloat(TaskField.ACTUAL_DURATION.getName)
  val actualFinish = CustomDate(TaskField.ACTUAL_FINISH.getName)

  val startAsSoonAsPossible = CustomDate(ConstraintType.AS_SOON_AS_POSSIBLE.name())
  val startAsLateAsPossible = CustomDate(ConstraintType.AS_LATE_AS_POSSIBLE.name())
  val mustStartOn = CustomDate(ConstraintType.MUST_START_ON.name())
  val mustFinishOn = CustomDate(ConstraintType.MUST_FINISH_ON.name())
  val startNoEarlierThan = CustomDate(ConstraintType.START_NO_EARLIER_THAN.name())
  val startNoLaterThan = CustomDate(ConstraintType.START_NO_LATER_THAN.name())
  val finishNoEarlierThan = CustomDate(ConstraintType.FINISH_NO_EARLIER_THAN.name())
  val finishNoLaterThan = CustomDate(ConstraintType.FINISH_NO_LATER_THAN.name())

  val finish = CustomDate(TaskField.FINISH.name())
  val deadline = CustomDate(TaskField.DEADLINE.name())

  val fields = List(actualDuration, actualWork, actualFinish,
    Summary, Description, AssigneeFullName, closedOn,
    finishNoEarlierThan,
    finishNoLaterThan,
    mustStartOn, mustFinishOn,
    priority, percentageComplete,
    startNoEarlierThan,
    startNoLaterThan,
    startAsSoonAsPossible,
    startAsLateAsPossible,
    taskDuration, taskWork, status, taskType,
    finish, deadline
  )
}
