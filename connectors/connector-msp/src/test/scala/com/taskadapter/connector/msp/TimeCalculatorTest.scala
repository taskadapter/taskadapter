package com.taskadapter.connector.msp

import com.taskadapter.model.{DoneRatio, EstimatedTime, GTask}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class TimeCalculatorTest extends FunSpec with Matchers {

  describe("time already spent") {
    it("0 on empty task") {
      TimeCalculator.calculateTimeAlreadySpent(new GTask()).getDuration shouldBe 0
    }

    it("0 on provided done ratio but empty estimated time") {
      TimeCalculator.calculateTimeAlreadySpent(new GTask().setValue(DoneRatio, 10f))
        .getDuration shouldBe 0
    }

    it("0 on provided time and empty done ratio") {
      TimeCalculator.calculateTimeAlreadySpent(new GTask().setValue(EstimatedTime, 10f))
        .getDuration shouldBe 0
    }

    it("3.5h when estimated time 7h and DoneRatio 50%") {
      TimeCalculator.calculateTimeAlreadySpent(new GTask()
        .setValue(EstimatedTime, 7f)
        .setValue(DoneRatio, 50f))
        .getDuration shouldBe 3.5
    }

    it("5h when estimated time 5h and DoneRatio 100%") {
      TimeCalculator.calculateTimeAlreadySpent(new GTask()
        .setValue(EstimatedTime, 5f)
        .setValue(DoneRatio, 100f))
        .getDuration shouldBe 5
    }

    it("0 when estimated time 5h and DoneRatio 0%") {
      TimeCalculator.calculateTimeAlreadySpent(new GTask()
        .setValue(EstimatedTime, 5f)
        .setValue(DoneRatio, 0f))
        .getDuration shouldBe 0
    }
  }

  describe("remaining time") {
    it("0 on empty task") {
      TimeCalculator.calculateRemainingTime(new GTask()).getDuration shouldBe 0
    }

    it("0 on provided done ratio but empty estimated time") {
      TimeCalculator.calculateRemainingTime(new GTask().setValue(DoneRatio, 10f))
        .getDuration shouldBe 0
    }

    it("10 when estimated time 10h and empty DoneRatio") {
      TimeCalculator.calculateRemainingTime(new GTask().setValue(EstimatedTime, 10f))
        .getDuration shouldBe 10
    }
    it("5h when estimated time 5h and DoneRatio 0%") {
      TimeCalculator.calculateRemainingTime(new GTask()
        .setValue(EstimatedTime, 5f)
        .setValue(DoneRatio, 0f))
        .getDuration shouldBe 5
    }

    it("2.5h when estimated time 10h and DoneRatio 75%") {
      TimeCalculator.calculateRemainingTime(new GTask()
        .setValue(EstimatedTime, 10f)
        .setValue(DoneRatio, 75f))
        .getDuration shouldBe 2.5
    }
  }
}