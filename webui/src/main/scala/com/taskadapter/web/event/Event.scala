package com.taskadapter.web.event

import java.util.UUID

import com.taskadapter.web.uiapi.{ConfigId, UISyncConfig}

sealed trait Event {
  // useful for debugging events sometimes
  val id = UUID.randomUUID().toString
}

case object AppStarted extends Event

case object SampleCaseObjectEvent extends Event

case class CredentialsChangeRequested(login: String, password: String) extends Event

case class ShowConfigPageRequested(configId: ConfigId) extends Event

case class ShowConfigsListPageRequested() extends Event

case class ShowAllExportResultsRequested(configId: ConfigId) extends Event

case class StartExportRequested(config: UISyncConfig) extends Event

case class ConfigSaveRequested(config: UISyncConfig) extends Event

case class ConfigCloneRequested(configId: ConfigId) extends Event

case class ConfigDeleteRequested(configId: ConfigId) extends Event

case object LogPanelInfoPrinted extends Event

trait ArtifactIsBusy extends Event {
  val configId: ConfigId
}

trait ArtifactIsNoLongerBusy extends Event {
  val configId: ConfigId
}

case class ProcessStarted(configId: ConfigId) extends ArtifactIsBusy

case class ProcessFinished(configId: ConfigId) extends ArtifactIsNoLongerBusy