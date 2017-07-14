package com.taskadapter.webui.uiapi

import com.taskadapter.connector.Field
import com.taskadapter.connector.definition.FieldMapping
import com.taskadapter.connector.jira.JiraField
import com.taskadapter.connector.redmine.RedmineField
import com.taskadapter.web.uiapi.NewConfigSuggester
import org.junit.runner.RunWith
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class NewConfigSuggesterTest extends FunSpec with ScalaFutures with Matchers {

  it("suggests all elements from left connector") {
    val list = NewConfigSuggester.suggestedFieldMappingsForNewConfig(
      RedmineField.getSuggestedCombinations(), JiraField.getSuggestedCombinations())

    list.size shouldBe 14
    list.contains(FieldMapping(RedmineField.assignee, JiraField.assignee, true, "")) shouldBe true
    list.contains(FieldMapping(RedmineField.targetVersion, Field(""), false, "")) shouldBe true
  }

  it("suggests all elements from right connector") {
    val list = NewConfigSuggester.suggestedFieldMappingsForNewConfig(
      JiraField.getSuggestedCombinations(), RedmineField.getSuggestedCombinations())

    list.size shouldBe 14
  }

}
