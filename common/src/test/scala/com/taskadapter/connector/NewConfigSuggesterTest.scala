package com.taskadapter.connector

import com.taskadapter.connector.NewConfigSuggester.suggestedFieldMappingsForNewConfig
import com.taskadapter.connector.definition.FieldMapping
import com.taskadapter.model._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class NewConfigSuggesterTest extends FunSpec with Matchers {

  private val summaryM = FieldMapping(Some(Summary), Some(Summary), true, null)
  private val descriptionM = FieldMapping(Some(Description), Some(Description), true, null)
  private val dueDateM = FieldMapping(Some(DueDate), Some(DueDate), true, null)

  it("suggests elements from left connector") {
    val list = suggestedFieldMappingsForNewConfig(
      Seq(Summary, Description, Assignee),
      Seq(Summary, Description))

    list should contain only(summaryM, descriptionM)
  }

  it("suggests elements from right connector") {
    val list = suggestedFieldMappingsForNewConfig(
      Seq(Summary, Description, DueDate),
      Seq(Summary, Description, Priority, DueDate))

    list should contain only(summaryM, descriptionM, dueDateM)
  }

  it("empty lists give empty result") {
    suggestedFieldMappingsForNewConfig(Seq(), Seq()) shouldBe empty
  }

  it("empty left list gives empty results") {
    suggestedFieldMappingsForNewConfig(Seq(), Seq(Summary)) shouldBe empty
  }

  it("empty right list gives empty results") {
    suggestedFieldMappingsForNewConfig(Seq(Summary), Seq()) shouldBe empty
  }

}
