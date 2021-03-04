package com.taskadapter.webui.uiapi

import com.taskadapter.common.ui.NewConfigSuggester
import com.taskadapter.common.ui.FieldMapping
import com.taskadapter.connector.jira.{JiraFactory, JiraField}
import com.taskadapter.connector.redmine.{RedmineFactory, RedmineField}
import com.taskadapter.model.{AssigneeLoginName, GUser}
import org.junit.runner.RunWith
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class NewConfigSuggesterTest extends FunSpec with ScalaFutures with Matchers {

  val defaultRedmineFields = new RedmineFactory().getDefaultFieldsForNewConfig
  val defaultJiraFields = new JiraFactory().getDefaultFieldsForNewConfig

  val jiraRedmineFieldsNumber = 8

  val noUser : GUser = null.asInstanceOf[GUser]

  it("suggests all elements from left connector") {
    val list = NewConfigSuggester.suggestedFieldMappingsForNewConfig(
      defaultRedmineFields,
      defaultJiraFields)

    list.size shouldBe jiraRedmineFieldsNumber
    list.contains(new FieldMapping(AssigneeLoginName, AssigneeLoginName, true, null)) shouldBe true
  }

  it("suggests all elements from right connector") {
    val list = NewConfigSuggester.suggestedFieldMappingsForNewConfig(
      defaultJiraFields,
      defaultRedmineFields)

    list.size shouldBe jiraRedmineFieldsNumber
  }

}
