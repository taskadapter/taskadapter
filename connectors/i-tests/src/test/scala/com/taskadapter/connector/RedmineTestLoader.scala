package com.taskadapter.connector

import com.taskadapter.connector.definition.TaskSaveResult
import com.taskadapter.redmineapi.RedmineManager
import com.taskadapter.redmineapi.bean.Issue

object RedmineTestLoader {
  def loadCreatedTask(mgr: RedmineManager, result: TaskSaveResult): Issue = {
    val remoteKeys = result.getRemoteKeys
    val remoteKey = remoteKeys.iterator.next
    mgr.getIssueManager.getIssueById(Integer.valueOf(remoteKey))
  }
}
