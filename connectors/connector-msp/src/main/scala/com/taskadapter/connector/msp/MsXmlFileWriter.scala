package com.taskadapter.connector.msp

import java.util

import com.taskadapter.connector.{Field, FieldRow}
import com.taskadapter.connector.common.DefaultValueSetter
import com.taskadapter.connector.definition.{SaveResultBuilder, TaskId}
import com.taskadapter.connector.definition.exceptions.BadConfigException
import com.taskadapter.connector.msp.write.{DateFinder, MSPDefaultFields, RealWriter, ResourceManager}
import com.taskadapter.model.GTask
import net.sf.mpxj.{ProjectFile, Task}

class MsXmlFileWriter(rows: Iterable[FieldRow]) {
  private val ALIAS_REMOTE_ID = "TA Remote ID"
  private val ALIAS_ISSUE_TYPE = "TA Task Type"
  private val ALIAS_ISSUE_STATUS = "TA Task Status"
  private val ALIAS_TARGET_VERSION = "TA Target Version"
  private val ALIAS_ENVIRONMENT = "TA Environment"
  /**
    * MPXJ can't set NULL/undefined time for "duration" or "work" fields so we have to use
    * these text fields to indicate that "duration" or "work" is UNDEFINED and not "0"
    */
  private val ALIAS_IS_DURATION_UNDEFINED = "TA isDurationUndefined"
  private val ALIAS_IS_WORK_UNDEFINED = "TA isWorkUndefined"

  def write(absoluteFilePath: String, syncResult: SaveResultBuilder, tasks: util.List[GTask], keepTaskId: Boolean)
  : String = { // XXX load resources from existing MS file to cache here
    val project = new ProjectFile
    project.setAutoTaskID(true)
    project.setAutoTaskUniqueID(!keepTaskId)
    project.setAutoResourceID(true)
    project.setAutoResourceUniqueID(true)
    project.setAutoOutlineLevel(true)
    project.setAutoOutlineNumber(true)
    setAliases(project)
    addTasks(syncResult, project, null, tasks, keepTaskId)
    val header = project.getProjectHeader
    project.addDefaultBaseCalendar
    val earliestTaskDate = DateFinder.findEarliestStartDate(project.getAllTasks)
    if (earliestTaskDate != null) header.setStartDate(earliestTaskDate)
    val taTag = "Project created by Task Adapter. http://www.taskadapter.com"
    /* setComments() does not work with MPXJ 4.0.0 and MS Project 2010 Prof.
              * I sent a bug report to MPXJ developer
              * header.setComments(taTag);
              */ header.setSubject(taTag)
    RealWriter.writeProject(absoluteFilePath, project)
  }

  @throws[BadConfigException]
  private def addTasks(syncResult: SaveResultBuilder, project: ProjectFile, parentMSPTask: Task, gTasks: util.List[GTask], keepTaskId: Boolean): Unit = {
    import scala.collection.JavaConversions._
    for (gTask <- gTasks) {
      val newMspTask = if (parentMSPTask == null) {
        project.addTask()
      } else {
        parentMSPTask.addTask()
      }
      val transformedTask = DefaultValueSetter.adapt(rows, gTask)
      val gTaskToMSP = new GTaskToMSP(newMspTask, new ResourceManager(project))
      gTaskToMSP.setFields(transformedTask, keepTaskId)
      syncResult.addCreatedTask(TaskId(gTask.getId, gTask.getKey), TaskId(newMspTask.getID.longValue(), newMspTask.getID + ""))
      addTasks(syncResult, project, newMspTask, transformedTask.getChildren, keepTaskId)
    }
  }

  private def setAliases(project: ProjectFile): Unit = {
    project.setTaskFieldAlias(MSPDefaultFields.FIELD_DURATION_UNDEFINED, ALIAS_IS_DURATION_UNDEFINED)
    project.setTaskFieldAlias(MSPDefaultFields.FIELD_WORK_UNDEFINED, ALIAS_IS_WORK_UNDEFINED)
    //        setAliasIfMappingNotNULL(project, FIELD.REMOTE_ID, ALIAS_REMOTE_ID);
    //        setAliasIfMappingNotNULL(project, Field.taskType, ALIAS_ISSUE_TYPE);
    //        setAliasIfMappingNotNULL(project, FIELD.TASK_STATUS, ALIAS_ISSUE_STATUS);
    //        setAliasIfMappingNotNULL(project, FIELD.ENVIRONMENT, ALIAS_ENVIRONMENT);
    //        setAliasIfMappingNotNULL(project, FIELD.TARGET_VERSION, ALIAS_TARGET_VERSION);
  }

  private def setAliasIfMappingNotNULL(project: ProjectFile, field: Field, aliasName: String): Unit = {
    val mspFileFieldName = field.name
    if (mspFileFieldName != null) {
      /* it is NULL if the old Task Adapter config does not have a mapping for this field.
                     * E.g. we added "task type" field in the new TA version and then we try running
                     * export using the old config, which does not have "task type" mapped to anything.
                     */ project.setTaskFieldAlias(MSPUtils.getTaskFieldByName(mspFileFieldName), aliasName)
    }
  }
}