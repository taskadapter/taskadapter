package com.taskadapter.connector.common

import java.util

import com.taskadapter.connector.FieldRow
import com.taskadapter.connector.common.data.ConnectorConverter
import com.taskadapter.connector.definition.{ConnectorConfig, ProgressMonitor, SaveResultBuilder}
import com.taskadapter.connector.definition.exceptions.ConnectorException
import com.taskadapter.core.PreviouslyCreatedTasksResolver
import com.taskadapter.model.GTask

object TaskSavingUtils {
  /**
    * Saves relations between tasks. Result builder must have correct
    * mappings between old and new task identifiers. Exceptions reported as a
    * general exceptions in a result builder.
    *
    * @param config to use. This config may prevent update of relations.
    * @param tasks tasks to save.
    * @param saver relation saver.
    * @param resultBuilder result builder.
    */
  def saveRemappedRelations(config: ConnectorConfig, tasks: util.List[GTask], saver: RelationSaver,
                            resultBuilder: SaveResultBuilder): Unit = {
    if (!config.getSaveIssueRelations) return
    val result = RelationUtils.convertRelationIds(tasks, resultBuilder)
    try
      saver.saveRelations(result)
    catch {
      case e: ConnectorException =>
        resultBuilder.addGeneralError(e)
    }
  }

  def saveTasks[N](previouslyCreatedTasks: PreviouslyCreatedTasksResolver,
                   tasks: util.List[GTask],
                   converter: ConnectorConverter[GTask, N],
                   saveAPI: BasicIssueSaveAPI[N],
                   progressMonitor: ProgressMonitor,
                   fieldRows: Iterable[FieldRow[_]],
                   targetLocation: String): SaveResultBuilder = {
    val result = new SaveResultBuilder
    val saver = new SimpleTaskSaver[N](previouslyCreatedTasks, converter, saveAPI, result, progressMonitor, targetLocation)
    val parentIssueId = None
    saver.saveTasks(parentIssueId, tasks, fieldRows)
    result
  }
}
