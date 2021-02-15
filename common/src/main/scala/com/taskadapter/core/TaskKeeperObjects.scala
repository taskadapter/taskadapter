package com.taskadapter.core

import com.taskadapter.connector.definition.TaskKeyMapping

case class TaskKeeperLocation(location1: String, location2: String, cacheFileLocation: String)

case class PreviouslyCreatedTasksCache(location1: String, location2: String, items: Seq[TaskKeyMapping])
