package com.taskadapter.connector.trello

import com.taskadapter.model.AllFields
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class TrelloFactoryTest extends FunSpec with Matchers {
  val defaultFieldsForNewConfig = new TrelloFactory().getDefaultFieldsForNewConfig

  describe("default fields") {
    it("have Trello list name") {
      defaultFieldsForNewConfig should contain(TrelloField.listName)
    }
    it("do not have these") {
      defaultFieldsForNewConfig should contain noneOf(TrelloField.listId, AllFields.reporterFullName,
        AllFields.reporterLoginName, AllFields.id, AllFields.key)
    }
  }
}