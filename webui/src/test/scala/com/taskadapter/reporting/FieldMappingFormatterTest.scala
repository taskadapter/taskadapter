package com.taskadapter.reporting

import com.taskadapter.connector.definition.FieldMapping
import com.taskadapter.model.{CustomDate, Description, DoneRatio, DueDate, Summary}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class FieldMappingFormatterTest extends FunSpec with Matchers {
  it("creates a nice string") {
    val result = FieldMappingFormatter.format(Seq(FieldMapping(Some(Summary), Some(Description), true, "abc"),
      FieldMapping(Some(DoneRatio), None, false, ""),
      FieldMapping(Some(DueDate), Some(CustomDate("some custom text")), false, "")
    ))
    result should include("DueDate                        - CustomDate(some custom text)   selected: false default:")
  }
}
