package com.taskadapter.connector.msp

import com.taskadapter.connector.msp.MSPTestUtils.load
import com.taskadapter.connector.testlib.TestUtils.findTaskByFieldName
import com.taskadapter.model.AllFields
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

import java.util.Calendar

@RunWith(classOf[JUnitRunner])
class DateTest extends FunSpec with Matchers {
  val gtasks = load("start_date_by_constraint.xml")

  it("start date must start on") {
    val gtask = findTaskByFieldName(gtasks, AllFields.summary, "must start on")
    gtask.getValue(MspField.mustStartOn) shouldBe createMSPDate(15, 9, 2011, 8)
  }

  it("start date no later than") {
    val gtask = findTaskByFieldName(gtasks, AllFields.summary, "start no later than")
    gtask.getValue(MspField.startNoLaterThan) shouldBe createMSPDate(10, 9, 2011, 8)
  }

  it("must finish on") {
    val gtask = findTaskByFieldName(gtasks, AllFields.summary, "must finish on")
    gtask.getValue(MspField.mustFinishOn) shouldBe createMSPDate(3, 12, 2011, 17)
  }

  private def createMSPDate(day: Int, month: Int, year: Int, hour: Int) = {
    val calendar = Calendar.getInstance
    calendar.clear()
    calendar.set(year, month - 1, day, hour, 0)
    calendar.getTime
  }
}
