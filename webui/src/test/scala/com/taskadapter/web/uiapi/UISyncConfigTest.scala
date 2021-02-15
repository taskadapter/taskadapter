package com.taskadapter.web.uiapi

import com.taskadapter.connector.definition.FieldMapping
import com.taskadapter.model.{AssigneeLoginName, Description, Summary}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class UISyncConfigTest extends FunSpec with Matchers {
  it("reverses mappings") {
    UISyncConfig.reverse(java.util.List.of(
      FieldMapping(Summary, Description, true, ""),
      FieldMapping(None, Some(AssigneeLoginName), true, "")
    )
    ) should contain only(FieldMapping(Description, Summary, true, ""), FieldMapping(Some(AssigneeLoginName), None, true, ""))
  }
}
