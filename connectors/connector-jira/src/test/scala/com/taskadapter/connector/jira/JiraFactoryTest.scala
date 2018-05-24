package com.taskadapter.connector.jira

import com.taskadapter.model.{Assignee, EstimatedTime, Reporter}
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
      new JiraFactory().getDefaultFieldsForNewConfig should not contain Reporter
      new JiraFactory().getDefaultFieldsForNewConfig should not contain EstimatedTime
    }
  }
}