package com.taskadapter.connector.msp

import java.util.Date

import com.taskadapter.connector.msp.write.ResourceManager
import com.taskadapter.model.GTask
import net.sf.mpxj._

import scala.collection.JavaConverters._

class GTaskToMSP(mspTask: Task, resourceManager: ResourceManager) {

  /**
    * "% done" field is used to calculate "actual work". this is more like a
    * hack used until Redmine REST API provides "time spent" serverInfo in
    * "issues list" response (see task http://www.redmine.org/issues/5303 )
    */
  def setFields(gTask: GTask, keepTaskId: Boolean): Unit = {
    mspTask.setMilestone(false)
    // TODO eliminate the boolean argument. split this function in two.
    if (keepTaskId) { // Setting the old unique id, true only in "save external id" operation
      // TODO TA3 this is probably obsolete and won't work with JIRA text IDs
      mspTask.setUniqueID(gTask.getId.toInt)
    }
    gTask.getFields.asScala.foreach { x =>
      processField(x._1, x._2)
    }
  }

  private def processField(fieldName: String, value: Any): Unit = {
    val stringBasedValue = CustomFieldConverter.getValueAsString(value)
    fieldName match {
      case MspField.summary.name => mspTask.setName(stringBasedValue)
      case MspField.description.name => mspTask.setNotes(stringBasedValue)
      case MspField.status.name => setFieldByName(fieldName, stringBasedValue)
      case MspField.taskType.name => setFieldByName(fieldName, stringBasedValue)
      case MspField.assignee.name => processAssignee(fieldName, stringBasedValue)
      case MspField.mustStartOn.name => mspTask.setStart(value.asInstanceOf[Date])
      case MspField.startAsSoonAsPossible.name =>
        mspTask.setConstraintType(ConstraintType.AS_SOON_AS_POSSIBLE)
        mspTask.setConstraintDate(value.asInstanceOf[Date])
      case MspField.startAsLateAsPossible.name =>
        mspTask.setConstraintType(ConstraintType.AS_LATE_AS_POSSIBLE)
        mspTask.setConstraintDate(value.asInstanceOf[Date])
      case MspField.startNoEarlierThan.name =>
        mspTask.setConstraintType(ConstraintType.START_NO_EARLIER_THAN)
        mspTask.setConstraintDate(value.asInstanceOf[Date])
      case MspField.startNoLaterThan.name =>
        mspTask.setConstraintType(ConstraintType.START_NO_LATER_THAN)
        mspTask.setConstraintDate(value.asInstanceOf[Date])
      case MspField.mustFinishOn.name =>
        mspTask.setConstraintType(ConstraintType.MUST_FINISH_ON)
        mspTask.setConstraintDate(value.asInstanceOf[Date])
      case MspField.finishNoEarlierThan.name =>
        mspTask.setConstraintType(ConstraintType.FINISH_NO_EARLIER_THAN)
        mspTask.setConstraintDate(value.asInstanceOf[Date])
      case MspField.finishNoLaterThan.name =>
        mspTask.setConstraintType(ConstraintType.FINISH_NO_LATER_THAN)
        mspTask.setConstraintDate(value.asInstanceOf[Date])

      case MspField.finish.name =>
        if (value != null) {
          mspTask.setFinish(value.asInstanceOf[Date])
        }
      case MspField.deadline.name =>
        if (value != null) {
          mspTask.setDeadline(value.asInstanceOf[Date])
        }
      case MspField.priority.name =>
        val mspPriority = Priority.getInstance(value.asInstanceOf[Int])
        mspTask.setPriority(mspPriority)

      case MspField.taskDuration.name => mspTask.setDuration(Duration.getInstance(value.asInstanceOf[Int], TimeUnit.HOURS))
      case MspField.taskWork.name => mspTask.setWork(Duration.getInstance(value.asInstanceOf[Int], TimeUnit.HOURS))
      case MspField.actualWork.name => mspTask.setActualWork(Duration.getInstance(value.asInstanceOf[Int], TimeUnit.HOURS))
      case MspField.actualDuration.name => mspTask.setActualDuration(Duration.getInstance(value.asInstanceOf[Int], TimeUnit.HOURS))
      case MspField.percentageComplete.name => mspTask.setPercentageComplete(value.asInstanceOf[Int])
      case MspField.actualFinish.name => mspTask.setActualFinish(value.asInstanceOf[Date])

      case _ => // unknown, ignore for now
    }
  }

  private def processAssignee(fieldName: String, value: Any) = {
    if (value != null) {

      val assigneeName = value.toString
      val resource = resourceManager.getOrCreateResource(assigneeName)
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

  private def setFieldByName(fieldName: String, value: Any): Unit = {
    val f = MSPUtils.getTaskFieldByName(fieldName)
    mspTask.set(f, value)
  }
}
