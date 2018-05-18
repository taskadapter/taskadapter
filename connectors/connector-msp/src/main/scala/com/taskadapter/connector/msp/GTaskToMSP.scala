package com.taskadapter.connector.msp

import java.util.Date

import com.taskadapter.connector.msp.write.ResourceManager
import com.taskadapter.model.{Priority => _, _}
import net.sf.mpxj._
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

class GTaskToMSP(mspTask: Task, resourceManager: ResourceManager) {
  private val log = LoggerFactory.getLogger(classOf[GTaskToMSP])
  /**
    * "% done" field is used to calculate "actual work". this is more like a
    * hack used until Redmine REST API provides "time spent" serverInfo in
    * "issues list" response (see task http://www.redmine.org/issues/5303 )
    */
  def setFields(gTask: GTask, keepTaskId: Boolean): Unit = {
    mspTask.setMilestone(false)
    if (keepTaskId) { // Setting the old unique id, true only in "save external id" operation
      mspTask.setUniqueID(gTask.getId.toInt)
    }
    gTask.getFields.asScala.foreach { x =>
      processField(x._1, x._2)
    }
  }

  private def processField(field: Field[_], value: Any): Unit = {
    val stringBasedValue = CustomFieldConverter.getValueAsString(value)
    field match {
      case Summary => mspTask.setName(stringBasedValue)
      case Description => mspTask.setNotes(stringBasedValue)
      case Assignee => processAssignee(value)
      case MspField.mustStartOn =>
        mspTask.setConstraintType(ConstraintType.MUST_START_ON)
        mspTask.setConstraintDate(value.asInstanceOf[Date])
      case MspField.startAsSoonAsPossible =>
        mspTask.setConstraintType(ConstraintType.AS_SOON_AS_POSSIBLE)
        mspTask.setConstraintDate(value.asInstanceOf[Date])
      case MspField.startAsLateAsPossible =>
        mspTask.setConstraintType(ConstraintType.AS_LATE_AS_POSSIBLE)
        mspTask.setConstraintDate(value.asInstanceOf[Date])
      case MspField.startNoEarlierThan =>
        mspTask.setConstraintType(ConstraintType.START_NO_EARLIER_THAN)
        mspTask.setConstraintDate(value.asInstanceOf[Date])
      case MspField.startNoLaterThan =>
        mspTask.setConstraintType(ConstraintType.START_NO_LATER_THAN)
        mspTask.setConstraintDate(value.asInstanceOf[Date])
      case MspField.mustFinishOn =>
        mspTask.setConstraintType(ConstraintType.MUST_FINISH_ON)
        mspTask.setConstraintDate(value.asInstanceOf[Date])
      case MspField.finishNoEarlierThan =>
        mspTask.setConstraintType(ConstraintType.FINISH_NO_EARLIER_THAN)
        mspTask.setConstraintDate(value.asInstanceOf[Date])
      case MspField.finishNoLaterThan =>
        mspTask.setConstraintType(ConstraintType.FINISH_NO_LATER_THAN)
        mspTask.setConstraintDate(value.asInstanceOf[Date])

      case MspField.finish =>
        if (value != null) {
          mspTask.setFinish(value.asInstanceOf[Date])
        }
      case MspField.deadline =>
        if (value != null) {
          mspTask.setDeadline(value.asInstanceOf[Date])
        }
      case com.taskadapter.model.Priority =>
        val mspPriority = Priority.getInstance(value.asInstanceOf[Int])
        mspTask.setPriority(mspPriority)

      case MspField.taskDuration => mspTask.setDuration(Duration.getInstance(value.asInstanceOf[Float], TimeUnit.HOURS))
      case MspField.taskWork => mspTask.setWork(Duration.getInstance(value.asInstanceOf[Float], TimeUnit.HOURS))
      case MspField.actualWork => mspTask.setActualWork(Duration.getInstance(value.asInstanceOf[Float], TimeUnit.HOURS))
      case MspField.actualDuration => mspTask.setActualDuration(Duration.getInstance(value.asInstanceOf[Float], TimeUnit.HOURS))
      case MspField.percentageComplete => mspTask.setPercentageComplete(value.asInstanceOf[Int])
      case MspField.actualFinish => mspTask.setActualFinish(value.asInstanceOf[Date])

      case other if other.name.startsWith("Text") =>
        setFieldByName(field, stringBasedValue)

      case unknown => // ignore for now
    }
  }

  private def processAssignee(value: Any): Unit = {
    if (value != null) {
      val user = value.asInstanceOf[GUser]
      val resource = resourceManager.getOrCreateResource(user.displayName)
      val ass = mspTask.addResourceAssignment(resource)
      ass.setUnits(100)
      // MUST set the remaining work to avoid this bug:
      //http://www.hostedredmine.com/issues/7780 "Duration" field is ignored when "Assignee" is set
      /*      if (gTask.getEstimatedHours != null) {
              ass.setRemainingWork(TimeCalculator.calculateRemainingTime(gTask))
              // the same "IF" as above, now for the resource assignment. this might need refactoring...
              if (gTask.getDoneRatio != null && mappings.isFieldSelected(DONE_RATIO)) {
                val timeAlreadySpent = TimeCalculator.calculateTimeAlreadySpent(gTask.getDoneRatio, gTask.getEstimatedHours)
                ass.setActualWork(timeAlreadySpent)
              }
            */
    }
  }

  private def setFieldByName(field: Field[_], value: Any): Unit = {
    val f = MSPUtils.getTaskFieldByName(field.name)
    mspTask.set(f, value)
  }
}
