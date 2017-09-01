package com.taskadapter.web

sealed trait SettingChangedEvent

case object SchedulerEnabledEvent extends SettingChangedEvent
case object SchedulerDisabledEvent extends SettingChangedEvent
