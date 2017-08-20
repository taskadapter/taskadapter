package com.taskadapter.model

import java.util.{Calendar, Date}

import com.taskadapter.connector.Field

import scala.util.Random

object GTaskBuilder {
  def withRandom(fieldName: String): GTask = {
    new GTaskBuilder().withRandom(Field("String", fieldName)).build()
  }
  def withRandom(field: Field): GTask = {
    new GTaskBuilder().withRandom(field).build()
  }
}

class GTaskBuilder {
  val task = new GTask

  def withRandom(field: Field): GTaskBuilder = {
    val value = field.typeName match {
      case "Date" => getDateRoundedToMinutes
      case "Float" => val value = Random.nextFloat() * 100
        // round to 2 digits
        val double = Math.round(value * 100.0) / 100.0
        double.toFloat
      case "GUser" => new GUser(null, Random.nextString(3), Random.nextString(10))
      case "String" => "value " + new Date().getTime

    }
    task.setValue(field, value)
    this
  }

  def build(): GTask = {
    task
  }

  def getDateRoundedToMinutes: Date = {
    val cal = Calendar.getInstance
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    cal.getTime
  }

}

