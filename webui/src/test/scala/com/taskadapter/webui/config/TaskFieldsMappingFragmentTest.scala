package com.taskadapter.webui.config

import com.taskadapter.connector.definition.FieldMapping
import com.taskadapter.model.{CreatedOn, UpdatedOn}
import com.taskadapter.webui.Page
import com.taskadapter.webui.uiapi.ConfigsTempFolder
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

import java.util

@RunWith(classOf[JUnitRunner])
class TaskFieldsMappingFragmentTest extends FunSpec with Matchers with ConfigsTempFolder {
  it("empty default value for non-string-based field is converted to empty string") {
    val f = new TaskFieldsMappingFragment(Page.MESSAGES,
      util.Arrays.asList(CreatedOn), Page.MESSAGES, "JIRA",
      util.Arrays.asList(CreatedOn, UpdatedOn), Page.MESSAGES, "Redmine",
      Seq(FieldMapping(CreatedOn, CreatedOn, true, null))
    )

    f.getElements.size shouldBe 1
    f.getElements.head.defaultValue shouldBe ""
  }
}
