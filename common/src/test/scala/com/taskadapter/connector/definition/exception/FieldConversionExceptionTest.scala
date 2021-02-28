package com.taskadapter.connector.definition.exception

import com.taskadapter.model.Summary
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class FieldConversionExceptionTest extends FunSpec with Matchers {
  describe("good message") {
    it("empty collection") {
      new FieldConversionException("JIRA", Summary, Seq(), "").getMessage should include("Empty collection cannot")
    }
    it("collection with items") {
      new FieldConversionException("JIRA", Summary, Seq("component1", "component2"), "")
        .getMessage should include("Collection of (component1,component2) cannot")
    }

    it("integer value") {
      new FieldConversionException("JIRA", Summary, 123, "")
        .getMessage should include("Value '123' cannot")
    }
  }
}
