package com.taskadapter.web

import java.util.prefs.Preferences

class SettingsManager {
  private val LICENSE_AGREEMENT_FLAG = "task_adapter_license_agreement_accepted"
  private val DEFAULT_LICENSE_AGREEMENT_ACCEPTED = false

  private val ERROR_REPORTING_ENABLED = "task_adapter.error_reporting_enabled"
  private val ERROR_REPORTING_DEFAULT_STATE = true

  private val FIELD_IS_LOCAL_MODE = "TALocal"
  private val DEFAULT_LOCAL = true
  private val ALLOW_MANAGE_ALL_CONFIG = "admin_can_see_all_configs"
  private val SCHEDULER_ENABLED = "scheduler_enabled"
  private val MAX_NUMBER_RESULTS_TO_KEEP = "max_number_of_results_to_keep"
  private val DEFAULT_MAX_NUMBER_OF_RESULTS = 100000

  private val listeners = new java.util.ArrayList[SettingListener]()

  def registerListener(listener: SettingListener): Unit = listeners.add(listener)

  private val prefs = Preferences.userNodeForPackage(classOf[SettingsManager])

  def isTAWorkingOnLocalMachine: Boolean = prefs.getBoolean(FIELD_IS_LOCAL_MODE, DEFAULT_LOCAL)

  def setLocal(local: Boolean): Unit = prefs.putBoolean(FIELD_IS_LOCAL_MODE, local)

  def isLicenseAgreementAccepted: Boolean = prefs.getBoolean(LICENSE_AGREEMENT_FLAG, DEFAULT_LICENSE_AGREEMENT_ACCEPTED)

  def isErrorReportingEnabled: Boolean = prefs.getBoolean(ERROR_REPORTING_ENABLED, ERROR_REPORTING_DEFAULT_STATE)

  def markLicenseAgreementAsAccepted(): Unit = prefs.putBoolean(LICENSE_AGREEMENT_FLAG, true)

  def setErrorReporting(enabled: Boolean): Unit = prefs.putBoolean(ERROR_REPORTING_ENABLED, enabled)

  def getMaxNumberOfResultsToKeep: Int = prefs.getInt(MAX_NUMBER_RESULTS_TO_KEEP, DEFAULT_MAX_NUMBER_OF_RESULTS)

  def setMaxNumberOfResultsToKeep(value: Int): Unit = prefs.putInt(MAX_NUMBER_RESULTS_TO_KEEP, value)

  def adminCanManageAllConfigs: Boolean = prefs.getBoolean(ALLOW_MANAGE_ALL_CONFIG, false)

  def setAdminCanManageAllConfigs(canManage: Boolean): Unit = prefs.putBoolean(ALLOW_MANAGE_ALL_CONFIG, canManage)

  def schedulerEnabled: Boolean = prefs.getBoolean(SCHEDULER_ENABLED, false)

  def setSchedulerEnabled(flag: Boolean): Unit = {
    prefs.putBoolean(SCHEDULER_ENABLED, flag)
    notifyListeners(if (flag) SchedulerEnabledEvent else SchedulerDisabledEvent)
  }

  private def notifyListeners(event: SettingChangedEvent): Unit =
    listeners.forEach(listener => listener.settingChanged(event))
}