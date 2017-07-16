package com.taskadapter.connector.msp

import java.util.Calendar

import com.taskadapter.connector.msp.MSPTestUtils.load
import com.taskadapter.connector.testlib.TestUtils.findTaskByFieldName
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

import scala.collection.JavaConverters._


@RunWith(classOf[JUnitRunner])
class DateTest extends FunSpec with Matchers {
  val gtasks = load("start_date_by_constraint.xml").asScala.toList

  // TODO TA3 MSP start date tests
  it("startDateMustStartOn") {
    val gtask = findTaskByFieldName(gtasks, MspField.summary.name, "must start on")
    //        assertEquals(createMSPDate(15, 9, 2011, 8), gtask.getStartDate());
  }

  it("startDateNoLaterThan") {
    val gtask = findTaskByFieldName(gtasks, MspField.summary.name, "start no later than")
    //        assertEquals(createMSPDate(10, 9, 2011, 8), gtask.getStartDate());
  }

  it("startDateMustFinishOn") {
    val gtask = findTaskByFieldName(gtasks, MspField.summary.name, "must finish on")
    //        assertEquals(createMSPDate(3, 12, 2011, 17), gtask.getStartDate());
  }

  private def createMSPDate(day: Int, month: Int, year: Int, hour: Int) = {
    val calendar = Calendar.getInstance
    calendar.clear()
    calendar.set(year, month - 1, day, hour, 0)
    calendar.getTime
  }
}
