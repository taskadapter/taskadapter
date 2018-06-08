package com.taskadapter.connector.testlib

import java.util.{Calendar, Date}

object DateUtils {

  def getDateRoundedToMinutes: Date = {
    val cal = Calendar.getInstance
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    cal.getTime
  }

  def getDateRoundedToDay: Date = {
    getCalendarRoundedToDay.getTime
  }

  def getCalendarRoundedToDay: Calendar = {
    val cal = Calendar.getInstance
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal
  }
}
