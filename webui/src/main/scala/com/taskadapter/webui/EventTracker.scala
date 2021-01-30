package com.taskadapter.webui

import com.taskadapter.web.event.{ApplicationActionEvent, ApplicationActionEventWithValue, EventBusImpl}

object EventTracker {
  def trackEvent(category: EventCategory, action: String, label: String): Unit = {
    EventBusImpl.post(ApplicationActionEvent(category, action, label))
  }

  def trackEvent(category: EventCategory, action: String, label: String, value: Int): Unit = {
    EventBusImpl.post(ApplicationActionEventWithValue(category, action, label, value))
  }
}
