package com.taskadapter.schedule

import java.util.concurrent.{ScheduledThreadPoolExecutor, TimeUnit}

import com.taskadapter.connector.common.ProgressMonitorUtils
import com.taskadapter.web.uiapi.{UIConfigStore, UISyncConfig}
import com.taskadapter.webui.TALog
import com.taskadapter.webui.pages.ExportResultsLogger
import com.taskadapter.webui.results.ExportResultStorage

class ScheduleRunner(uiConfigStore: UIConfigStore, exportResultStorage: ExportResultStorage) {
  val log = TALog.log

  val threadsNumber = 1
  val initialDelaySec = 5
  val intervalSec = 30

  def start(): Unit = {
    log.info(s"Starting scheduler to support periodic sync. Time interval is $intervalSec sec.")

    val ex = new ScheduledThreadPoolExecutor(threadsNumber)
    val task = new Runnable {

      def run() = {
        val configs = uiConfigStore.getConfigs()
        val scheduled = configs.filter(c => c.schedule.directionLeft || c.schedule.directionRight)
        log.info(s"Found ${configs.size} configs, ${scheduled.size} scheduled for periodic execution") //. ${allResults.size} results total.")
        scheduled.foreach { c =>
          if (needToRun(c)) {
            launchSyncLeftOrRight(c)
          }
        }
      }
    }
    val f = ex.scheduleAtFixedRate(task, initialDelaySec, intervalSec, TimeUnit.SECONDS)
  }

  def needToRun(c: UISyncConfig): Boolean = {
    val results = exportResultStorage.getSaveResults(c.id)
    if (results.isEmpty) {
      true
    } else {
      val lastResult = results.maxBy(r => r.dateStarted.getTime + r.timeTookSeconds * 1000)
      val now = System.currentTimeMillis()
      val lastRanLong = lastResult.dateStarted.getTime + lastResult.timeTookSeconds * 1000
      val timePassedMin = (now - lastRanLong) * 1000 * 60
      val needtoRun = timePassedMin > c.schedule.intervalInMinutes
      needtoRun
    }
  }

  def launchSyncLeftOrRight(c: UISyncConfig) = {
    if (c.schedule.directionRight) {
      launchSync(c)
    } else if (c.schedule.directionLeft) {
      launchSync(c.reverse)
    } else {
      log.error(s"Config is scheduled for periodic sync, but its scheduled direction is neither right nor left. Broken config? Config: $c")
    }
  }

  def launchSync(c: UISyncConfig) = {
    val loaded = UISyncConfig.loadTasks(c, 10000)
    val result = c.saveTasks(loaded, ProgressMonitorUtils.DUMMY_MONITOR)
    ExportResultsLogger.log(result, prefix = "Scheduled export completed.")
    exportResultStorage.store(result)
  }
}