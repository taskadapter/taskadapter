package com.taskadapter.connector.common

import com.taskadapter.model.{CustomDate, Priority, Summary}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class FieldPrettyNameBuilderTest extends FunSpec with Matchers {
  describe("pretty name generator") {
    it("custom field") {
      FieldPrettyNameBuilder.getPrettyFieldName(CustomDate("Actual Finish")) shouldBe "CustomDate(Actual Finish)"
    }
    it("regular field") {
      FieldPrettyNameBuilder.getPrettyFieldName(Summary) shouldBe "Summary"
    }
    it("priority") {
      FieldPrettyNameBuilder.getPrettyFieldName(Priority) shouldBe "Priority"
    }
  }
}
