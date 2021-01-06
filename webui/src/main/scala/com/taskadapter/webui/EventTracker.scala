package com.taskadapter.webui

import com.taskadapter.web.event.{ApplicationActionEvent, ApplicationActionEventWithValue, EventBusImpl, PageShown}

object EventTracker {
  def trackPage(name: String): Unit = {
    EventBusImpl.post(PageShown(name))
  }

  def trackEvent(category: EventCategory, action: String, label: String): Unit = {
    EventBusImpl.post(ApplicationActionEvent(category, action, label))
  }

  def trackEvent(category: EventCategory, action: String, label: String, value: Int): Unit = {
    EventBusImpl.post(ApplicationActionEventWithValue(category, action, label, value))
  }
}
