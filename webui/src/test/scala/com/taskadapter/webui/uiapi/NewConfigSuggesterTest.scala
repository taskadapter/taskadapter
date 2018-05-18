package com.taskadapter.webui.uiapi

import com.taskadapter.connector.NewConfigSuggester
import com.taskadapter.connector.definition.FieldMapping
import com.taskadapter.connector.jira.JiraField
import com.taskadapter.connector.redmine.RedmineField
import com.taskadapter.model.{Assignee, Field, GUser, TargetVersion}
import org.junit.runner.RunWith
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class NewConfigSuggesterTest extends FunSpec with ScalaFutures with Matchers {

  val jiraRedmineFieldsNumber = 15

  val noUser : GUser = null.asInstanceOf[GUser]

  it("suggests all elements from left connector") {
    val list = NewConfigSuggester.suggestedFieldMappingsForNewConfig(
      RedmineField.fields, JiraField.fields)

    list.size shouldBe jiraRedmineFieldsNumber
    list.contains(FieldMapping(Assignee, Assignee, true, noUser)) shouldBe true
    list.contains(FieldMapping(TargetVersion, Field(""), false, "")) shouldBe true
  }

  it("suggests all elements from right connector") {
    val list = NewConfigSuggester.suggestedFieldMappingsForNewConfig(
      JiraField.fields, RedmineField.fields)

    list.size shouldBe jiraRedmineFieldsNumber
  }

}
