package com.taskadapter.web.uiapi

import scala.beans.BeanProperty

object Schedule {
  def apply: Schedule = new Schedule(60, false, false)
}

case class Schedule(@BeanProperty var intervalInMinutes: Int,
                    @BeanProperty var directionLeft: Boolean,
                    @BeanProperty var directionRight: Boolean)
