package com.taskadapter.web.uiapi

import com.taskadapter.connector.definition.FieldMapping
import com.taskadapter.model.{Assignee, Description, Summary}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class UISyncConfigTest extends FunSpec with Matchers {
  it("reverses mappings") {
    UISyncConfig.reverse(Seq(
      FieldMapping(Summary, Description, true, ""),
      FieldMapping(None, Some(Assignee), true, "")
    )
    ) should contain only(FieldMapping(Description, Summary, true, ""), FieldMapping(Some(Assignee), None, true, ""))
  }
}