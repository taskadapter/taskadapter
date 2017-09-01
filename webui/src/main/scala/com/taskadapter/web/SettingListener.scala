package com.taskadapter.web

trait SettingListener {
  def settingChanged(settingEvent: SettingChangedEvent) : Unit
}
