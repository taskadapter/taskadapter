package com.taskadapter.model

sealed trait StandardField

case object Assignee extends StandardField

case object ClosedOn extends StandardField

case object CreatedOn extends StandardField

case object EstimatedTime extends StandardField

case object Description extends StandardField

case object DoneRatio extends StandardField

case object DueDate extends StandardField

case object Id extends StandardField

case object Priority extends StandardField

case object StartDate extends StandardField

case object Summary extends StandardField

case object TaskType extends StandardField

case object TaskStatus extends StandardField

case object TargetVersion extends StandardField

case object UpdatedOn extends StandardField
