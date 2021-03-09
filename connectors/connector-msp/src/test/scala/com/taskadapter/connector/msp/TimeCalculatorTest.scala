package com.taskadapter.connector.msp

import com.taskadapter.model.{AllFields, GTask}
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
      TimeCalculator.calculateTimeAlreadySpent(new GTask().setValue(AllFields.doneRatio, java.lang.Float.valueOf(10f)))
        .getDuration shouldBe 0
    }

    it("0 on provided time and empty done ratio") {
      TimeCalculator.calculateTimeAlreadySpent(new GTask().setValue(AllFields.estimatedTime, java.lang.Float.valueOf(10f)))
        .getDuration shouldBe 0
    }

    it("3.5h when estimated time 7h and DoneRatio 50%") {
      TimeCalculator.calculateTimeAlreadySpent(new GTask()
        .setValue(AllFields.estimatedTime, java.lang.Float.valueOf(7f))
        .setValue(AllFields.doneRatio, java.lang.Float.valueOf(50f)))
        .getDuration shouldBe 3.5
    }

    it("5h when estimated time 5h and DoneRatio 100%") {
      TimeCalculator.calculateTimeAlreadySpent(new GTask()
        .setValue(AllFields.estimatedTime, java.lang.Float.valueOf(5f))
        .setValue(AllFields.doneRatio, java.lang.Float.valueOf(100f)))
        .getDuration shouldBe 5
    }

    it("0 when estimated time 5h and DoneRatio 0%") {
      TimeCalculator.calculateTimeAlreadySpent(new GTask()
        .setValue(AllFields.estimatedTime, java.lang.Float.valueOf(5f))
        .setValue(AllFields.doneRatio, java.lang.Float.valueOf(0f)))
        .getDuration shouldBe 0
    }
  }

  describe("remaining time") {
    it("0 on empty task") {
      TimeCalculator.calculateRemainingTime(new GTask()).getDuration shouldBe 0
    }

    it("0 on provided done ratio but empty estimated time") {
      TimeCalculator.calculateRemainingTime(new GTask().setValue(AllFields.doneRatio, java.lang.Float.valueOf(10f)))
        .getDuration shouldBe 0
    }

    it("10 when estimated time 10h and empty DoneRatio") {
      TimeCalculator.calculateRemainingTime(new GTask().setValue(AllFields.estimatedTime, java.lang.Float.valueOf(10F)))
        .getDuration shouldBe 10
    }
    it("5h when estimated time 5h and DoneRatio 0%") {
      TimeCalculator.calculateRemainingTime(new GTask()
        .setValue(AllFields.estimatedTime, java.lang.Float.valueOf(5f))
        .setValue(AllFields.doneRatio, java.lang.Float.valueOf(0f)))
        .getDuration shouldBe 5
    }

    it("2.5h when estimated time 10h and DoneRatio 75%") {
      TimeCalculator.calculateRemainingTime(new GTask()
        .setValue(AllFields.estimatedTime, java.lang.Float.valueOf(10f))
        .setValue(AllFields.doneRatio, java.lang.Float.valueOf(75f)))
        .getDuration shouldBe 2.5
    }
  }
}