package com.taskadapter.schedule

import java.util.concurrent.{ScheduledThreadPoolExecutor, TimeUnit}

import com.taskadapter.connector.common.ProgressMonitorUtils
import com.taskadapter.connector.definition.exceptions.BadConfigException
import com.taskadapter.web.uiapi.{Schedule, UIConfigStore, UISyncConfig}
import com.taskadapter.web.{SchedulerDisabledEvent, SchedulerEnabledEvent, SettingsManager}
import com.taskadapter.webui.pages.ExportResultsLogger
import com.taskadapter.webui.results.ExportResultStorage
import com.taskadapter.webui.{SchedulesStorage, TALog}

class ScheduleRunner(uiConfigStore: UIConfigStore, schedulesStorage: SchedulesStorage,
                     exportResultStorage: ExportResultStorage,
                     settingsManager: SettingsManager) {
  val log = TALog.log

  val threadsNumber = 1
  val initialDelaySec = 30
  val intervalSec = 20

  var allowedToRun = settingsManager.schedulerEnabled
  var currentlyBusy = false

  settingsManager.registerListener {
    case SchedulerEnabledEvent => start()
    case SchedulerDisabledEvent => stop()
    case _ => // ignore other events
  }

  init()

  def init(): Unit = {
    log.info(s"Configuring scheduler to support periodic sync. Time interval is $intervalSec sec. 'enabled' flag is: $allowedToRun")

    val ex = new ScheduledThreadPoolExecutor(threadsNumber)
    val task = new Runnable {

      def run() = {
        if (allowedToRun) {
          val schedules = schedulesStorage.getSchedules()
          if (schedules.nonEmpty) {
            log.info(s"Found ${schedules.size} scheduled configs. Checking if it is time for them to run...")
            schedules.foreach { s =>
              if (needToRun(s)) {
                launchSyncLeftOrRight(s)
              }
            }
          } else {
            log.debug("Scheduler is enabled in the application settings, but there are no scheduled tasks to process. " +
              "You can define some tasks on 'Schedules' page or disable the scheduler to avoid this message.")
          }
        }
      }
    }
    val f = ex.scheduleAtFixedRate(task, initialDelaySec, intervalSec, TimeUnit.SECONDS)
  }

  def stop(): Unit = {
    log.info("Stopping scheduler")
    allowedToRun = false
  }

  def start(): Unit = {
    log.info("Starting scheduler")
    allowedToRun = true
  }

  def needToRun(schedule: Schedule): Boolean = {
    val results = exportResultStorage.getSaveResults(schedule.configId)
    if (results.isEmpty) {
      true
    } else {
      val lastResult = results.maxBy(r => r.dateStarted.getTime + r.timeTookSeconds * 1000)
      val now = System.currentTimeMillis()
      val lastRanLong = lastResult.dateStarted.getTime + lastResult.timeTookSeconds * 1000
      val timePassedSeconds = (now - lastRanLong) / 1000
      val needtoRun = timePassedSeconds >= (schedule.intervalInMinutes * 60)
      needtoRun
    }
  }

  private def launchSyncLeftOrRight(s: Schedule): Unit = {
    if (currentlyBusy) {
      log.info(s"Skipping scheduled sync for $s because another sync is currently running")
    } else {
      currentlyBusy = true
      try {
        val config = uiConfigStore.getConfig(s.configId)
        if (config.isDefined) {
          if (s.directionRight) {
            launchSync(config.get)
          }

          if (s.directionLeft) {
            launchSync(config.get.reverse)
          }
        } else {
          log.error(s"Cannot find config ${s.configId} scheduled for execution in $s. Skipping it.")
        }
      } finally {
        currentlyBusy = false
      }
    }
  }

  private def launchSync(c: UISyncConfig): Unit = {
    log.info(s"Starting scheduled export from ${c.getConnector1.getLabel} to ${c.getConnector2.getLabel}")

    try {
      val errors = c.getConnector1.validateForLoad()
      if (errors.isEmpty) {
        c.getConnector2.validateForSave(c.fieldMappings)

        val loaded = UISyncConfig.loadTasks(c, 10000)
        val result = c.saveTasks(loaded, ProgressMonitorUtils.DUMMY_MONITOR)
        ExportResultsLogger.log(result, prefix = "Scheduled export completed.")
        exportResultStorage.store(result)
      } else {
        logErrors(c, errors)
      }
    } catch {
      case e: BadConfigException => log.error(s"Config ${c.configId.id} is scheduled for periodic export, " +
        s"but it will be skipped because it failed load or save validation: $e")
      case other => log.error(s"Config ${c.configId.id} scheduled sync: Error $other")
    }
  }

  private def logErrors(c: UISyncConfig, errors: Seq[BadConfigException]) : Unit = {
    log.error(s"Config ${c.configId.id} is scheduled for periodic export, " +
      s"but it will be skipped because it failed load or save validation: $errors")
  }
}