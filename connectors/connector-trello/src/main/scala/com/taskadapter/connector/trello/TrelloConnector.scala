package com.taskadapter.connector.trello

import java.util

import com.julienvey.trello.domain.TList
import com.taskadapter.connector.common.TaskSavingUtils
import com.taskadapter.connector.definition._
import com.taskadapter.connector.{FieldRow, NewConnector}
import com.taskadapter.core.PreviouslyCreatedTasksResolver
import com.taskadapter.model.GTask
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

object TrelloConnector {
  val logger = LoggerFactory.getLogger(classOf[TrelloConnector])
  /**
    * Keep it the same to enable backward compatibility with the existing
    * config files.
    */
  val ID = "Trello"
}

class TrelloConnector(config: TrelloConfig, setup: WebConnectorSetup) extends NewConnector {

  val trelloApi = TrelloApiFactory.createApi(setup.getPassword, setup.getApiKey)

  override def loadTaskByKey(key: TaskId, rows: java.lang.Iterable[FieldRow[_]]): GTask = {
    val loader = new TrelloTaskLoader(trelloApi)
    loader.loadTask(config, key.getKey)
  }

  override def loadData: util.List[GTask] = {
    val loader = new TrelloTaskLoader(trelloApi)
    loader.loadTasks(config).asJava
  }

  def loadLists(boardId: String): Seq[TList] = {
    trelloApi.getBoardLists(boardId).asScala
  }

  override def saveData(previouslyCreatedTasks: PreviouslyCreatedTasksResolver, tasks: util.List[GTask], monitor: ProgressMonitor,
                        rows: java.lang.Iterable[FieldRow[_]]): SaveResult = {
    val lists = loadLists(config.getBoardId)
    val converter = new GTaskToTrello(config, new ListCache(lists))
    val saver = new TrelloTaskSaver(trelloApi)
    val rb = TaskSavingUtils.saveTasks(previouslyCreatedTasks, tasks, converter, saver, monitor, rows,
      setup.getHost)
    rb.getResult

  }
}