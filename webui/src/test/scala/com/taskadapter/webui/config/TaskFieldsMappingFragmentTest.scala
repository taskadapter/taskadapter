package com.taskadapter.webui.config

import com.taskadapter.connector.definition.FieldMapping
import com.taskadapter.model.{Assignee, Reporter}
import com.taskadapter.webui.Page
import com.taskadapter.webui.uiapi.ConfigsTempFolder
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class TaskFieldsMappingFragmentTest extends FunSpec with Matchers with ConfigsTempFolder {
  it("empty default value for non-string-based field is converted to null") {
    val f = new TaskFieldsMappingFragment(Page.MESSAGES,
      Seq(Assignee), "JIRA",
      Seq(Assignee, Reporter), "Redmine",
      Seq(FieldMapping(Assignee, Assignee, true, null))
    )

    f.getElements.size shouldBe 1
    org.junit.Assert.assertNull(f.getElements.head.defaultValue)
  }
}
