package com.taskadapter.connector.common

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class ValueTypeResolverTest extends FunSpec with Matchers {
  describe("getValueAsString") {
    it("null to empty") {
      ValueTypeResolver.getValueAsString(null) shouldBe ""
    }
    it("string to string") {
      ValueTypeResolver.getValueAsString("abc") shouldBe "abc"
    }
    it("empty Seq to empty") {
      ValueTypeResolver.getValueAsString(Seq()) shouldBe ""
    }
    it("Seq of strings to first string") {
      ValueTypeResolver.getValueAsString(Seq("a", "b")) shouldBe "a"
    }
  }
}