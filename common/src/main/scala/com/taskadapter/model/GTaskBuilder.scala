package com.taskadapter.model

import java.util.{Calendar, Date}

import scala.util.Random

object GTaskBuilder {
  def withRandom(fieldName: String): GTask = {
    new GTaskBuilder().withRandom(Field(fieldName)).build()
  }

  def withRandom(field: Field[_]): GTask = {
    new GTaskBuilder().withRandom(field).build()
  }
}

class GTaskBuilder {
  val task = new GTask

  def withField[T](field: Field[T], value: T): GTaskBuilder = {
    task.setValue(field, value)
    this
  }

  def withRandom(field: Field[_]): GTaskBuilder = {
    field match {
      case x: CustomDate => task.setValue(x, getDateRoundedToMinutes)
      case x: CustomFloat => val value = Random.nextFloat() * 100
        //       round to 2 digits
        val double = Math.round(value * 100.0) / 100.0
        task.setValue(x, double.floatValue())
      //      case "GUser" => new GUser(null, Random.nextString(3), Random.nextString(10))
      //      case "String" => "value " + new Date().getTime
      case Summary => task.setValue(Summary, randomStr())
      case Description => task.setValue(Description, randomStr())
      case Assignee => task.setValue(Assignee, new GUser(null, Random.nextString(3), Random.nextString(10)))
      case x: CustomString => task.setValue(x, "value " + new Date().getTime)
    }
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

  def randomStr(): String = "value " + new Date().getTime
}

