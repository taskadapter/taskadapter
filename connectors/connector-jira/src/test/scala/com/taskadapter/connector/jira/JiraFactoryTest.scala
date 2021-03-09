package com.taskadapter.connector.jira

import com.taskadapter.model.{AllFields, AssigneeLoginName, DueDate, EstimatedTime, Id, Key, ReporterLoginName}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpec, Matchers}

@RunWith(classOf[JUnitRunner])
class JiraFactoryTest extends FunSpec with Matchers {
  describe("default fields") {
    it("have Assignee") {
      new JiraFactory().getDefaultFieldsForNewConfig should contain(AllFields.assigneeLoginName)
    }
    it("skip optional ones") {
      new JiraFactory().getDefaultFieldsForNewConfig should contain noneOf(AllFields.reporterLoginName,
        AllFields.estimatedTime,
        AllFields.dueDate,
        AllFields.id, AllFields.key)
    }
  }
}