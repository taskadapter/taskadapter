package com.taskadapter.connector.redmine

import com.taskadapter.model.AllFields
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class RedmineFactoryTest extends FunSpec with Matchers {
  describe("default fields") {
    it("have Assignee") {
      new RedmineFactory().getDefaultFieldsForNewConfig should contain(AllFields.assigneeLoginName)
    }
    it("skip optional ones") {
      new RedmineFactory().getDefaultFieldsForNewConfig should contain noneOf(AllFields.updatedOn,  AllFields.id,  AllFields.key)
    }
  }
}