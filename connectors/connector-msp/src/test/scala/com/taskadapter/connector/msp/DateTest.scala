package com.taskadapter.connector.msp

import java.util.Calendar

import com.taskadapter.connector.msp.MSPTestUtils.load
import com.taskadapter.connector.testlib.TestUtils.findTaskByFieldName
import com.taskadapter.model.Summary
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

import scala.collection.JavaConverters._

@RunWith(classOf[JUnitRunner])
class DateTest extends FunSpec with Matchers {
  val gtasks = load("start_date_by_constraint.xml").asScala

  it("start date must start on") {
    val gtask = findTaskByFieldName(gtasks, Summary, "must start on")
    gtask.getValue(MspField.mustStartOn) shouldBe createMSPDate(15, 9, 2011, 8)
  }

  it("start date no later than") {
    val gtask = findTaskByFieldName(gtasks, Summary, "start no later than")
    gtask.getValue(MspField.startNoLaterThan) shouldBe createMSPDate(10, 9, 2011, 8)
  }

  it("must finish on") {
    val gtask = findTaskByFieldName(gtasks, Summary, "must finish on")
    gtask.getValue(MspField.mustFinishOn) shouldBe createMSPDate(3, 12, 2011, 17)
  }

  private def createMSPDate(day: Int, month: Int, year: Int, hour: Int) = {
    val calendar = Calendar.getInstance
    calendar.clear()
    calendar.set(year, month - 1, day, hour, 0)
    calendar.getTime
  }
}
