package com.taskadapter.connector.jira

import com.taskadapter.model.{Assignee, DueDate, EstimatedTime, Reporter}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class JiraFactoryTest extends FunSpec with Matchers {
  describe("default fields") {
    it("have Assignee") {
      new JiraFactory().getDefaultFieldsForNewConfig should contain(Assignee)
    }
    it("skip optional ones") {
      new JiraFactory().getDefaultFieldsForNewConfig should contain noneOf (Reporter, EstimatedTime, DueDate)
    }
  }
}