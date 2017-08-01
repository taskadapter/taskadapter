package com.taskadapter.connector.msp

import com.taskadapter.connector.FieldRow
import com.taskadapter.connector.msp.write.{ResourceManager, TaskEstimationMode}
import net.sf.mpxj.Task
import com.taskadapter.connector.FieldRow
import com.taskadapter.connector.definition.Mappings
import com.taskadapter.connector.definition.exceptions.BadConfigException
import com.taskadapter.connector.msp.MspField
import com.taskadapter.model.GTask
import com.taskadapter.model.GTaskDescriptor
import com.taskadapter.model.GUser
import net.sf.mpxj.ConstraintType
import net.sf.mpxj.Duration
import net.sf.mpxj.Priority
import net.sf.mpxj.Resource
import net.sf.mpxj.ResourceAssignment
import net.sf.mpxj.Task
import net.sf.mpxj.TaskField
import net.sf.mpxj.TimeUnit
import com.taskadapter.model.GTaskDescriptor.FIELD._
import scala.collection.JavaConverters._

class GTaskToMSP(mspTask: Task, resourceManager: ResourceManager) {
  private val DEFAULT_HOURS_FOR_NONESTIMATED_TASK = 8

  /*
    private def getTaskEstimationMode(gTask: GTask, mappings: Mappings) = {
      // Normal case, time is mapped and set.
      if (gTask.getEstimatedHours != null && mappings.isFieldSelected(ESTIMATED_TIME)) return TaskEstimationMode.TASK_TIME
      // "%% Done" is ignored by MSP if there's no estimate on task.
      // This makes sense, but unfortunately some users want "% done" to be transferred even when
      // there's no time estimate.
      if (mappings.isFieldSelected(DONE_RATIO) && gTask.getDoneRatio != null) {
        //  Estimation time is set. Use it even if user does not ask to
        // map estimated time. It is still more reasonable than "wild guess" estimation.
        if (gTask.getEstimatedHours != null) return TaskEstimationMode.TASK_TIME
        return TaskEstimationMode.WILD_GUESS
      }
      TaskEstimationMode.NO_ESTIMATE
    }
  */

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
//    processEstimatedTime(gTask)
//    processDoneRatio(gTask)
//    processPriority(gTask)
//    processKey(gTask)
//    processStartDate(gTask)
//    processDueDate(gTask)
//    processClosedDate(gTask)
    //    setFieldIfSelected(TARGET_VERSION, mspTask, gTask.getTargetVersionName)
  }

  private def processField(fieldName: String, value: Any) = {
    val stringBasedValue = CustomFieldConverter.getValueAsString(value)
    fieldName match {
      case MspField.summary.name => mspTask.setName(stringBasedValue)
      case MspField.description.name => mspTask.setNotes(stringBasedValue)
      case MspField.status.name => setFieldByName(fieldName, stringBasedValue)
      case MspField.taskType.name => setFieldByName(fieldName, stringBasedValue)
      case MspField.assignee.name => processAssignee(fieldName,  stringBasedValue)
      case _ => // unknown, ignore for now
    }
  }

/*  private def processKey(gTask: GTask) = {
    setFieldIfSelected(REMOTE_ID, mspTask, gTask.getKey)
  }

  private def processPriority(gTask: GTask) = {
    if (mappings.isFieldSelected(PRIORITY) && gTask.getPriority != null) {
      val mspPriority = Priority.getInstance(gTask.getPriority)
      mspTask.setPriority(mspPriority)
    }
  }

  private def processDueDate(gTask: GTask) = {
    if (gTask.getDueDate != null && mappings.isFieldSelected(DUE_DATE)) {
      val dueDateValue = mappings.getMappedTo(DUE_DATE)
      if (dueDateValue == TaskField.FINISH.toString) mspTask.set(TaskField.FINISH, gTask.getDueDate)
      else if (dueDateValue == TaskField.DEADLINE.toString) mspTask.set(TaskField.DEADLINE, gTask.getDueDate)
    }
  }

  private def processStartDate(gTask: GTask) = {
    if (mappings.isFieldSelected(START_DATE)) {
      val constraint = mappings.getMappedTo(START_DATE)
      if (constraint == null || MSPUtils.NO_CONSTRAINT == constraint) mspTask.setStart(gTask.getStartDate)
      else {
        val constraintType = ConstraintType.valueOf(constraint)
        mspTask.setConstraintType(constraintType)
        mspTask.setConstraintDate(gTask.getStartDate)
      }
    }
  }

  private def processClosedDate(gTask: GTask) = {
    if (mappings.isFieldSelected(CLOSE_DATE)) {
      val constraint = mappings.getMappedTo(CLOSE_DATE)
      if (TaskField.ACTUAL_FINISH.getName == constraint) mspTask.setActualFinish(gTask.getClosedDate)
    }
  }
*/
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

  private def processEstimatedTime(gTask: GTask) = {
/*
    val estimatedTime = calculateTaskEstimatedTime(gTask)
    getTaskEstimationMode(gTask) match {
      case TASK_TIME =>
        setEstimatedHours(estimatedTime)
        break //todo: break is not supported
      case WILD_GUESS =>
        setEstimatedHours(estimatedTime)
        // "estimated" means that the time was "approximate". it is shown by MSP as "?" next to the duration value.
        // like "8 hrs?"
        mspTask.setEstimated(true)
        break //todo: break is not supported
      case NO_ESTIMATE =>
        mspTask.set(MSPDefaultFields.FIELD_DURATION_UNDEFINED, "true")
        mspTask.set(MSPDefaultFields.FIELD_WORK_UNDEFINED, "true")
        break //todo: break is not supported
    }
*/
  }

  /**
    * Calculates a task estimated time. In some cases (for example, exporting
    * DONE_RATIO but no estimated time is set) we still need to use some
    * estimation. This method respects that cases and can provide general task
    * estimation. If task estimation is not required, returns null.
    *
    * @param gTask task to estimate it time.
    * @return <code>null</code> if task does not require an estimated time.
    *         Otherwise estimated (or guessed) task time.
    */
  private def calculateTaskEstimatedTime(gTask: GTask) = {
 /*   val estimationMode = getTaskEstimationMode(gTask)
    estimationMode match {
      case TASK_TIME =>
        return gTask.getEstimatedHours
      case WILD_GUESS =>
        return TaskFieldsSetter.DEFAULT_HOURS_FOR_NONESTIMATED_TASK
      case NO_ESTIMATE =>
        return null
    }
    throw new IncompatibleClassChangeError("Bad/unsupported estimation mode " + estimationMode)*/
  }

/*
  private def getTaskEstimationMode(gTask: GTask) = TaskFieldsSetter.getTaskEstimationMode(gTask, mappings)

  @throws[BadConfigException]
  private def setEstimatedHours(hours: Float) = {
    val estimatedValue = Duration.getInstance(hours, TimeUnit.HOURS)
    if (MSPUtils.useWork(mappings)) {
      mspTask.setWork(estimatedValue)
      // need to explicitly clear it because we can have previously created
      // tasks with this field set to TRUE
      mspTask.set(MSPDefaultFields.FIELD_WORK_UNDEFINED, "false")
    }
    else {
      mspTask.setDuration(estimatedValue)
      mspTask.set(MSPDefaultFields.FIELD_DURATION_UNDEFINED, "false")
    }
    // processDoneRatio() logic used to be called from here until http://www.hostedredmine.com/issues/199534
    // was requested by Matt.
    // now I moved the call to the main setFields() so that Done Ratio is always set even if the estimated time is null.
  }
*/

/*
  private def processDoneRatio(gTask: GTask) = {
    if (gTask.getDoneRatio != null && mappings.isFieldSelected(DONE_RATIO)) {
      val estimatedTime = calculateTaskEstimatedTime(gTask)
      val timeAlreadySpent = TimeCalculator.calculateTimeAlreadySpent(gTask.getDoneRatio, estimatedTime)
      if (MSPUtils.useWork(mappings)) mspTask.setActualWork(timeAlreadySpent)
      else mspTask.setActualDuration(timeAlreadySpent)
      mspTask.setPercentageComplete(gTask.getDoneRatio)
    }
  }
*/

 /* private def setFieldIfSelected(field: GTaskDescriptor.FIELD, mspTask: Task, value: Any) = {
    if (mappings.isFieldSelected(field)) {
      val v = mappings.getMappedTo(field)
      val f = MSPUtils.getTaskFieldByName(v)
      mspTask.set(f, value)
    }
  }*/
 private def setFieldByName(fieldName: String, value: Any) = {
      val f = MSPUtils.getTaskFieldByName(fieldName)
      mspTask.set(f, value)
  }
}
