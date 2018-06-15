package com.taskadapter.webui

class NoOpGATracker extends Tracker {
  override def trackPage(name: String) = {}

  override def trackEvent(category: EventCategory, action: String, label: String): Unit = {}
}
