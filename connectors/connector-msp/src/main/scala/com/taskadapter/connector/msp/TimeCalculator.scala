package com.taskadapter.connector.msp

import com.taskadapter.model.{DoneRatio, EstimatedTime, GTask}
import net.sf.mpxj.{Duration, TimeUnit}

object TimeCalculator {
  def calculateTimeAlreadySpent(gTask: GTask): Duration = {
    val doneRatioInPercents = gTask.getValue(DoneRatio)
    val hours = gTask.getValue(EstimatedTime)

    val doneRatio = doneRatioInPercents / 100f
    val duration = doneRatio * hours
    Duration.getInstance(duration, TimeUnit.HOURS)
  }

  def calculateRemainingTime(gTask: GTask): Duration = {
    val timeAlreadySpent = calculateTimeAlreadySpent(gTask)
    val estimatedHours = gTask.getValue(EstimatedTime)
    val hoursLeft = estimatedHours - timeAlreadySpent.getDuration
    Duration.getInstance(hoursLeft, TimeUnit.HOURS)
  }
}