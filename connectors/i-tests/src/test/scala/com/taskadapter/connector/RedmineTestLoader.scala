package com.taskadapter.connector

import com.taskadapter.connector.definition.SaveResult
import com.taskadapter.redmineapi.RedmineManager
import com.taskadapter.redmineapi.bean.Issue

object RedmineTestLoader {
  /**
   use [[com.taskadapter.connector.testlib.TestSaver]]
   */
  @Deprecated
  def loadCreatedTask(mgr: RedmineManager, result: SaveResult): Issue = {
    val remoteKeys = result.getRemoteKeys
    val remoteKey = remoteKeys.iterator.next.id
    mgr.getIssueManager.getIssueById(remoteKey.toInt)
  }
}
