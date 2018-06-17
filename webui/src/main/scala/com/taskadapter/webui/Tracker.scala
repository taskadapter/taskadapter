package com.taskadapter.webui

sealed trait EventCategory {
  def name: String
}

case object WebAppCategory extends EventCategory {
  override def name: String = "webapp"
}

case object SetupCategory extends EventCategory {
  override def name: String = "setup"
}

case object ConfigCategory extends EventCategory {
  override def name: String = "config"
}

case object LicenseCategory extends EventCategory {
  override def name: String = "license"
}

case object ExportCategory extends EventCategory {
  override def name: String = "export"
}

case object UserCategory extends EventCategory {
  override def name: String = "user"
}

trait Tracker {
  /** Tracks a page view. */
  def trackPage(name: String): Unit

  def trackEvent(category: EventCategory, action: String, label: String): Unit

  def trackEvent(category: EventCategory, action: String, label: String, value: Integer): Unit
}