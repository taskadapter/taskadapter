package com.taskadapter.connector.msp

import com.taskadapter.model.{AllFields, GTask}
import net.sf.mpxj.{Duration, TimeUnit}

object TimeCalculator {
  def calculateTimeAlreadySpent(gTask: GTask): Duration = {
    val doneRatioInPercents = gTask.getValue(AllFields.doneRatio)
    val hours = gTask.getValue(AllFields.estimatedTime)

    val doneRatio = doneRatioInPercents / 100f
    val duration = doneRatio * hours
    Duration.getInstance(duration, TimeUnit.HOURS)
  }

  def calculateRemainingTime(gTask: GTask): Duration = {
    val timeAlreadySpent = calculateTimeAlreadySpent(gTask)
    val estimatedHours = gTask.getValue(AllFields.estimatedTime)
    val hoursLeft = estimatedHours - timeAlreadySpent.getDuration
    Duration.getInstance(hoursLeft, TimeUnit.HOURS)
  }
}