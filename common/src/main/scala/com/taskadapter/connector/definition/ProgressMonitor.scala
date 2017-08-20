package com.taskadapter.connector.definition

trait ProgressMonitor {
  def beginTask(taskName: String, total: Int): Unit

  def worked(work: Int): Unit

  def done(): Unit
}
